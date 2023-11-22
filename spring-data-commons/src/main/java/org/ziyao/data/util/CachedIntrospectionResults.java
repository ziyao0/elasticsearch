package org.ziyao.data.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInfoFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ziyao zhang
 * @since 2023/11/21
 */
public final class CachedIntrospectionResults {

    /**
     * System property that instructs Spring to use the {@link Introspector#IGNORE_ALL_BEANINFO}
     * mode when calling the JavaBeans {@link Introspector}: "spring.beaninfo.ignore", with a
     * value of "true" skipping the search for {@code BeanInfo} classes (typically for scenarios
     * where no such classes are being defined for beans in the application in the first place).
     * <p>The default is "false", considering all {@code BeanInfo} metadata classes, like for
     * standard {@link Introspector#getBeanInfo(Class)} calls. Consider switching this flag to
     * "true" if you experience repeated ClassLoader access for non-existing {@code BeanInfo}
     * classes, in case such access is expensive on startup or on lazy loading.
     * <p>Note that such an effect may also indicate a scenario where caching doesn't work
     * effectively: Prefer an arrangement where the Spring jars live in the same ClassLoader
     * as the application classes, which allows for clean caching along with the application's
     * lifecycle in any case. For a web application, consider declaring a local
     * {@link org.springframework.web.util.IntrospectorCleanupListener} in {@code web.xml}
     * in case of a multi-ClassLoader layout, which will allow for effective caching as well.
     *
     * @see Introspector#getBeanInfo(Class, int)
     */
    public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";


    private static final boolean shouldIntrospectorIgnoreBeaninfoClasses =
            SpringProperties.getFlag(IGNORE_BEANINFO_PROPERTY_NAME);

    /**
     * Stores the BeanInfoFactory instances.
     */
    private static final List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(
            BeanInfoFactory.class, CachedIntrospectionResults.class.getClassLoader());

    private static final Log logger = LogFactory.getLog(CachedIntrospectionResults.class);

    /**
     * Set of ClassLoaders that this CachedIntrospectionResults class will always
     * accept classes from, even if the classes do not qualify as cache-safe.
     */
    static final Set<ClassLoader> acceptedClassLoaders =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    /**
     * Map keyed by Class containing CachedIntrospectionResults, strongly held.
     * This variant is being used for cache-safe bean classes.
     */
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> strongClassCache =
            new ConcurrentHashMap<>(64);

    /**
     * Map keyed by Class containing CachedIntrospectionResults, softly held.
     * This variant is being used for non-cache-safe bean classes.
     */
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> softClassCache =
            new ConcurrentReferenceHashMap<>(64);


    /**
     * Accept the given ClassLoader as cache-safe, even if its classes would
     * not qualify as cache-safe in this CachedIntrospectionResults class.
     * <p>This configuration method is only relevant in scenarios where the Spring
     * classes reside in a 'common' ClassLoader (e.g. the system ClassLoader)
     * whose lifecycle is not coupled to the application. In such a scenario,
     * CachedIntrospectionResults would by default not cache any of the application's
     * classes, since they would create a leak in the common ClassLoader.
     * <p>Any {@code acceptClassLoader} call at application startup should
     * be paired with a {@link #clearClassLoader} call at application shutdown.
     *
     * @param classLoader the ClassLoader to accept
     */
    public static void acceptClassLoader(@Nullable ClassLoader classLoader) {
        if (classLoader != null) {
            acceptedClassLoaders.add(classLoader);
        }
    }

    /**
     * Clear the introspection cache for the given ClassLoader, removing the
     * introspection results for all classes underneath that ClassLoader, and
     * removing the ClassLoader (and its children) from the acceptance list.
     *
     * @param classLoader the ClassLoader to clear the cache for
     */
    public static void clearClassLoader(@Nullable ClassLoader classLoader) {
        acceptedClassLoaders.removeIf(registeredLoader ->
                isUnderneathClassLoader(registeredLoader, classLoader));
        strongClassCache.keySet().removeIf(beanClass ->
                isUnderneathClassLoader(beanClass.getClassLoader(), classLoader));
        softClassCache.keySet().removeIf(beanClass ->
                isUnderneathClassLoader(beanClass.getClassLoader(), classLoader));
    }

    /**
     * Create CachedIntrospectionResults for the given bean class.
     *
     * @param beanClass the bean class to analyze
     * @return the corresponding CachedIntrospectionResults
     * @throws BeansException in case of introspection failure
     */
    static CachedIntrospectionResults forClass(Class<?> beanClass) throws BeansException {
        CachedIntrospectionResults results = strongClassCache.get(beanClass);
        if (results != null) {
            return results;
        }
        results = softClassCache.get(beanClass);
        if (results != null) {
            return results;
        }

        results = new CachedIntrospectionResults(beanClass);
        ConcurrentMap<Class<?>, CachedIntrospectionResults> classCacheToUse;

        if (ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) ||
                isClassLoaderAccepted(beanClass.getClassLoader())) {
            classCacheToUse = strongClassCache;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
            }
            classCacheToUse = softClassCache;
        }

        CachedIntrospectionResults existing = classCacheToUse.putIfAbsent(beanClass, results);
        return (existing != null ? existing : results);
    }

    /**
     * Check whether this CachedIntrospectionResults class is configured
     * to accept the given ClassLoader.
     *
     * @param classLoader the ClassLoader to check
     * @return whether the given ClassLoader is accepted
     * @see #acceptClassLoader
     */
    private static boolean isClassLoaderAccepted(ClassLoader classLoader) {
        for (ClassLoader acceptedLoader : acceptedClassLoaders) {
            if (isUnderneathClassLoader(classLoader, acceptedLoader)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the given ClassLoader is underneath the given parent,
     * that is, whether the parent is within the candidate's hierarchy.
     *
     * @param candidate the candidate ClassLoader to check
     * @param parent    the parent ClassLoader to check for
     */
    private static boolean isUnderneathClassLoader(@Nullable ClassLoader candidate, @Nullable ClassLoader parent) {
        if (candidate == parent) {
            return true;
        }
        if (candidate == null) {
            return false;
        }
        ClassLoader classLoaderToCheck = candidate;
        while (classLoaderToCheck != null) {
            classLoaderToCheck = classLoaderToCheck.getParent();
            if (classLoaderToCheck == parent) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve a {@link BeanInfo} descriptor for the given target class.
     *
     * @param beanClass the target class to introspect
     * @return the resulting {@code BeanInfo} descriptor (never {@code null})
     * @throws IntrospectionException from the underlying {@link Introspector}
     */
    private static BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
            BeanInfo beanInfo = beanInfoFactory.getBeanInfo(beanClass);
            if (beanInfo != null) {
                return beanInfo;
            }
        }

        BeanInfo beanInfo = (shouldIntrospectorIgnoreBeaninfoClasses ?
                Introspector.getBeanInfo(beanClass, Introspector.IGNORE_ALL_BEANINFO) :
                Introspector.getBeanInfo(beanClass));
        BeanInfo beanInfo1 = Introspector.getBeanInfo(beanClass);

        BeanInfo beanInfo2 = Introspector.getBeanInfo(beanClass, Introspector.IGNORE_ALL_BEANINFO);
        // Immediately remove class from Introspector cache to allow for proper garbage
        // collection on class loader shutdown; we cache it in CachedIntrospectionResults
        // in a GC-friendly manner. This is necessary (again) for the JDK ClassInfo cache.
        Class<?> classToFlush = beanClass;
        do {
            Introspector.flushFromCaches(classToFlush);
            classToFlush = classToFlush.getSuperclass();
        }
        while (classToFlush != null && classToFlush != Object.class);

        return beanInfo;
    }


    /**
     * The BeanInfo object for the introspected bean class.
     */
    private final BeanInfo beanInfo;

    /**
     * PropertyDescriptor objects keyed by property name String.
     */
    private final Map<String, PropertyDescriptor> propertyDescriptors;

    /**
     * TypeDescriptor objects keyed by PropertyDescriptor.
     */
    private final ConcurrentMap<PropertyDescriptor, TypeDescriptor> typeDescriptorCache;


    /**
     * Create a new CachedIntrospectionResults instance for the given class.
     *
     * @param beanClass the bean class to analyze
     * @throws BeansException in case of introspection failure
     */
    private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
            }
            this.beanInfo = getBeanInfo(beanClass);

            if (logger.isTraceEnabled()) {
                logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
            }
            this.propertyDescriptors = new LinkedHashMap<>();

            Set<String> readMethodNames = new HashSet<>();

            // This call is slow so we do it once.
            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if (Class.class == beanClass && !("name".equals(pd.getName()) ||
                        (pd.getName().endsWith("Name") && String.class == pd.getPropertyType()))) {
                    // Only allow all name variants of Class properties
                    continue;
                }
                if (URL.class == beanClass && "content".equals(pd.getName())) {
                    // Only allow URL attribute introspection, not content resolution
                    continue;
                }
                if (pd.getWriteMethod() == null && isInvalidReadOnlyPropertyType(pd.getPropertyType(), beanClass)) {
                    // Ignore read-only properties such as ClassLoader - no need to bind to those
                    continue;
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Found bean property '" + pd.getName() + "'" +
                            (pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") +
                            (pd.getPropertyEditorClass() != null ?
                                    "; editor [" + pd.getPropertyEditorClass().getName() + "]" : ""));
                }
                pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                this.propertyDescriptors.put(pd.getName(), pd);
                Method readMethod = pd.getReadMethod();
                if (readMethod != null) {
                    readMethodNames.add(readMethod.getName());
                }
            }

            // Explicitly check implemented interfaces for setter/getter methods as well,
            // in particular for Java 8 default methods...
            Class<?> currClass = beanClass;
            while (currClass != null && currClass != Object.class) {
                introspectInterfaces(beanClass, currClass, readMethodNames);
                currClass = currClass.getSuperclass();
            }

            // Check for record-style accessors without prefix: e.g. "lastName()"
            // - accessor method directly referring to instance field of same name
            // - same convention for component accessors of Java 15 record classes
            introspectPlainAccessors(beanClass, readMethodNames);

            this.typeDescriptorCache = new ConcurrentReferenceHashMap<>();
        } catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
        }
    }

    private void introspectInterfaces(Class<?> beanClass, Class<?> currClass, Set<String> readMethodNames)
            throws IntrospectionException {

        for (Class<?> ifc : currClass.getInterfaces()) {
            if (!ClassUtils.isJavaLanguageInterface(ifc)) {
                for (PropertyDescriptor pd : getBeanInfo(ifc).getPropertyDescriptors()) {
                    PropertyDescriptor existingPd = this.propertyDescriptors.get(pd.getName());
                    if (existingPd == null ||
                            (existingPd.getReadMethod() == null && pd.getReadMethod() != null)) {
                        // GenericTypeAwarePropertyDescriptor leniently resolves a set* write method
                        // against a declared read method, so we prefer read method descriptors here.
                        pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                        if (pd.getWriteMethod() == null &&
                                isInvalidReadOnlyPropertyType(pd.getPropertyType(), beanClass)) {
                            // Ignore read-only properties such as ClassLoader - no need to bind to those
                            continue;
                        }
                        this.propertyDescriptors.put(pd.getName(), pd);
                        Method readMethod = pd.getReadMethod();
                        if (readMethod != null) {
                            readMethodNames.add(readMethod.getName());
                        }
                    }
                }
                introspectInterfaces(ifc, ifc, readMethodNames);
            }
        }
    }

    private void introspectPlainAccessors(Class<?> beanClass, Set<String> readMethodNames)
            throws IntrospectionException {

        for (Method method : beanClass.getMethods()) {
            if (!this.propertyDescriptors.containsKey(method.getName()) &&
                    !readMethodNames.contains(method.getName()) && isPlainAccessor(method)) {
                this.propertyDescriptors.put(method.getName(),
                        new GenericTypeAwarePropertyDescriptor(beanClass, method.getName(), method, null, null));
                readMethodNames.add(method.getName());
            }
        }
    }

    private boolean isPlainAccessor(Method method) {
        if (Modifier.isStatic(method.getModifiers()) ||
                method.getDeclaringClass() == Object.class || method.getDeclaringClass() == Class.class ||
                method.getParameterCount() > 0 || method.getReturnType() == void.class ||
                isInvalidReadOnlyPropertyType(method.getReturnType(), method.getDeclaringClass())) {
            return false;
        }
        try {
            // Accessor method referring to instance field of same name?
            method.getDeclaringClass().getDeclaredField(method.getName());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isInvalidReadOnlyPropertyType(@Nullable Class<?> returnType, Class<?> beanClass) {
        return (returnType != null && (ClassLoader.class.isAssignableFrom(returnType) ||
                ProtectionDomain.class.isAssignableFrom(returnType) ||
                (AutoCloseable.class.isAssignableFrom(returnType) &&
                        !AutoCloseable.class.isAssignableFrom(beanClass))));
    }


    BeanInfo getBeanInfo() {
        return this.beanInfo;
    }

    Class<?> getBeanClass() {
        return this.beanInfo.getBeanDescriptor().getBeanClass();
    }

    @Nullable
    PropertyDescriptor getPropertyDescriptor(String name) {
        PropertyDescriptor pd = this.propertyDescriptors.get(name);
        if (pd == null && StringUtils.hasLength(name)) {
            // Same lenient fallback checking as in Property...
            pd = this.propertyDescriptors.get(StringUtils.uncapitalize(name));
            if (pd == null) {
                pd = this.propertyDescriptors.get(StringUtils.capitalize(name));
            }
        }
        return pd;
    }

    PropertyDescriptor[] getPropertyDescriptors() {
        return this.propertyDescriptors.values().toArray(PropertyDescriptorUtils.EMPTY_PROPERTY_DESCRIPTOR_ARRAY);
    }

    private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class<?> beanClass, PropertyDescriptor pd) {
        try {
            return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(),
                    pd.getWriteMethod(), pd.getPropertyEditorClass());
        } catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", ex);
        }
    }

    TypeDescriptor addTypeDescriptor(PropertyDescriptor pd, TypeDescriptor td) {
        TypeDescriptor existing = this.typeDescriptorCache.putIfAbsent(pd, td);
        return (existing != null ? existing : td);
    }

    @Nullable
    TypeDescriptor getTypeDescriptor(PropertyDescriptor pd) {
        return this.typeDescriptorCache.get(pd);
    }

}