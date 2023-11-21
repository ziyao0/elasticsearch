package org.springframework.data.annotation;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;

/**
 * @author ziyao zhang
 * @since 2023/11/20
 */
abstract class ParserStrategyUtils {

    /**
     * Instantiate a class using an appropriate constructor and return the new
     * instance as the specified assignable type. The returned instance will
     * have {@link BeanClassLoaderAware}, {@link BeanFactoryAware},
     * {@link EnvironmentAware}, and {@link ResourceLoaderAware} contracts
     * invoked if they are implemented by the given object.
     *
     * @since 5.2
     */
    @SuppressWarnings("unchecked")
    static <T> T instantiateClass(Class<?> clazz, Class<T> assignableTo, Environment environment,
                                  ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {

        Assert.notNull(clazz, "Class must not be null");
        Assert.isAssignable(assignableTo, clazz);
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        ClassLoader classLoader = (registry instanceof ConfigurableBeanFactory ?
                ((ConfigurableBeanFactory) registry).getBeanClassLoader() : resourceLoader.getClassLoader());
        T instance = (T) createInstance(clazz, environment, resourceLoader, registry, classLoader);
        ParserStrategyUtils.invokeAwareMethods(instance, environment, resourceLoader, registry, classLoader);
        return instance;
    }

    private static Object createInstance(Class<?> clazz, Environment environment,
                                         ResourceLoader resourceLoader, BeanDefinitionRegistry registry,
                                         @Nullable ClassLoader classLoader) {

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 1 && constructors[0].getParameterCount() > 0) {
            try {
                Constructor<?> constructor = constructors[0];
                Object[] args = resolveArgs(constructor.getParameterTypes(),
                        environment, resourceLoader, registry, classLoader);
                return BeanUtils.instantiateClass(constructor, args);
            } catch (Exception ex) {
                throw new BeanInstantiationException(clazz, "No suitable constructor found", ex);
            }
        }
        return BeanUtils.instantiateClass(clazz);
    }

    private static Object[] resolveArgs(Class<?>[] parameterTypes,
                                        Environment environment, ResourceLoader resourceLoader,
                                        BeanDefinitionRegistry registry, @Nullable ClassLoader classLoader) {

        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = resolveParameter(parameterTypes[i], environment,
                    resourceLoader, registry, classLoader);
        }
        return parameters;
    }

    @Nullable
    private static Object resolveParameter(Class<?> parameterType,
                                           Environment environment, ResourceLoader resourceLoader,
                                           BeanDefinitionRegistry registry, @Nullable ClassLoader classLoader) {

        if (parameterType == Environment.class) {
            return environment;
        }
        if (parameterType == ResourceLoader.class) {
            return resourceLoader;
        }
        if (parameterType == BeanFactory.class) {
            return (registry instanceof BeanFactory ? registry : null);
        }
        if (parameterType == ClassLoader.class) {
            return classLoader;
        }
        throw new IllegalStateException("Illegal method parameter type: " + parameterType.getName());
    }

    private static void invokeAwareMethods(Object parserStrategyBean, Environment environment,
                                           ResourceLoader resourceLoader, BeanDefinitionRegistry registry, @Nullable ClassLoader classLoader) {

        if (parserStrategyBean instanceof Aware) {
            if (parserStrategyBean instanceof BeanClassLoaderAware && classLoader != null) {
                ((BeanClassLoaderAware) parserStrategyBean).setBeanClassLoader(classLoader);
            }
            if (parserStrategyBean instanceof BeanFactoryAware && registry instanceof BeanFactory) {
                ((BeanFactoryAware) parserStrategyBean).setBeanFactory((BeanFactory) registry);
            }
            if (parserStrategyBean instanceof EnvironmentAware) {
                ((EnvironmentAware) parserStrategyBean).setEnvironment(environment);
            }
            if (parserStrategyBean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) parserStrategyBean).setResourceLoader(resourceLoader);
            }
        }
    }

}
