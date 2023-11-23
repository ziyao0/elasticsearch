package org.ziyao.data.convert.support;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.ziyao.data.annotation.ReflectionUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;


import java.lang.reflect.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class ObjectToObjectConverter implements ConditionalGenericConverter {

    // Cache for the latest to-method, static factory method, or factory constructor
    // resolved on a given Class
    private static final Map<Class<?>, Executable> conversionExecutableCache =
            new ConcurrentReferenceHashMap<>(32);


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return (sourceType.getType() != targetType.getType() &&
                hasConversionMethodOrConstructor(targetType.getType(), sourceType.getType()));
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Class<?> sourceClass = sourceType.getType();
        Class<?> targetClass = targetType.getType();
        Executable executable = getValidatedExecutable(targetClass, sourceClass);

        try {
            if (executable instanceof Method) {
                Method method = (Method) executable;
                ReflectionUtils.makeAccessible(method);
                if (!Modifier.isStatic(method.getModifiers())) {
                    return method.invoke(source);
                }
                else {
                    return method.invoke(null, source);
                }
            }
            else if (executable instanceof Constructor) {
                Constructor<?> ctor = (Constructor<?>) executable;
                ReflectionUtils.makeAccessible(ctor);
                return ctor.newInstance(source);
            }
        }
        catch (InvocationTargetException ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex.getTargetException());
        }
        catch (Throwable ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex);
        }

        // If sourceClass is Number and targetClass is Integer, the following message should expand to:
        // No toInteger() method exists on java.lang.Number, and no static valueOf/of/from(java.lang.Number)
        // method or Integer(java.lang.Number) constructor exists on java.lang.Integer.
        throw new IllegalStateException(String.format("No to%3$s() method exists on %1$s, " +
                        "and no static valueOf/of/from(%1$s) method or %3$s(%1$s) constructor exists on %2$s.",
                sourceClass.getName(), targetClass.getName(), targetClass.getSimpleName()));
    }


    static boolean hasConversionMethodOrConstructor(Class<?> targetClass, Class<?> sourceClass) {
        return (getValidatedExecutable(targetClass, sourceClass) != null);
    }

    @Nullable
    private static Executable getValidatedExecutable(Class<?> targetClass, Class<?> sourceClass) {
        Executable executable = conversionExecutableCache.get(targetClass);
        if (isApplicable(executable, sourceClass)) {
            return executable;
        }

        executable = determineToMethod(targetClass, sourceClass);
        if (executable == null) {
            executable = determineFactoryMethod(targetClass, sourceClass);
            if (executable == null) {
                executable = determineFactoryConstructor(targetClass, sourceClass);
                if (executable == null) {
                    return null;
                }
            }
        }

        conversionExecutableCache.put(targetClass, executable);
        return executable;
    }

    private static boolean isApplicable(Executable executable, Class<?> sourceClass) {
        if (executable instanceof Method) {
            Method method = (Method) executable;
            return (!Modifier.isStatic(method.getModifiers()) ?
                    ClassUtils.isAssignable(method.getDeclaringClass(), sourceClass) :
                    method.getParameterTypes()[0] == sourceClass);
        }
        else if (executable instanceof Constructor) {
            Constructor<?> ctor = (Constructor<?>) executable;
            return (ctor.getParameterTypes()[0] == sourceClass);
        }
        else {
            return false;
        }
    }

    @Nullable
    private static Method determineToMethod(Class<?> targetClass, Class<?> sourceClass) {
        if (String.class == targetClass || String.class == sourceClass) {
            // Do not accept a toString() method or any to methods on String itself
            return null;
        }

        Method method = ClassUtils.getMethodIfAvailable(sourceClass, "to" + targetClass.getSimpleName());
        return (method != null && !Modifier.isStatic(method.getModifiers()) &&
                ClassUtils.isAssignable(targetClass, method.getReturnType()) ? method : null);
    }

    @Nullable
    private static Method determineFactoryMethod(Class<?> targetClass, Class<?> sourceClass) {
        if (String.class == targetClass) {
            // Do not accept the String.valueOf(Object) method
            return null;
        }

        Method method = ClassUtils.getStaticMethod(targetClass, "valueOf", sourceClass);
        if (method == null) {
            method = ClassUtils.getStaticMethod(targetClass, "of", sourceClass);
            if (method == null) {
                method = ClassUtils.getStaticMethod(targetClass, "from", sourceClass);
            }
        }

        return (method != null && areRelatedTypes(targetClass, method.getReturnType()) ? method : null);
    }

    /**
     * Determine if the two types reside in the same type hierarchy (i.e., type 1
     * is assignable to type 2 or vice versa).
     * @since 5.3.21
     * @see ClassUtils#isAssignable(Class, Class)
     */
    private static boolean areRelatedTypes(Class<?> type1, Class<?> type2) {
        return (ClassUtils.isAssignable(type1, type2) || ClassUtils.isAssignable(type2, type1));
    }

    @Nullable
    private static Constructor<?> determineFactoryConstructor(Class<?> targetClass, Class<?> sourceClass) {
        return ClassUtils.getConstructorIfAvailable(targetClass, sourceClass);
    }

}
