package com.ziyao.springframework.data.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.*;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author ziyao zhang
 * @since 2023/11/20
 */
public abstract class TypeFilterUtils {

    /**
     * Create {@linkplain TypeFilter type filters} from the supplied
     * {@link org.springframework.core.annotation.AnnotationAttributes}, such as those sourced from
     * {@link ComponentScan#includeFilters()} or {@link ComponentScan#excludeFilters()}.
     * <p>Each {@link TypeFilter} will be instantiated using an appropriate
     * constructor, with {@code BeanClassLoaderAware}, {@code BeanFactoryAware},
     * {@code EnvironmentAware}, and {@code ResourceLoaderAware} contracts
     * invoked if they are implemented by the type filter.
     *
     * @param filterAttributes {@code AnnotationAttributes} for a
     *                         {@link ComponentScan.Filter @Filter} declaration
     * @param environment      the {@code Environment} to make available to filters
     * @param resourceLoader   the {@code ResourceLoader} to make available to filters
     * @param registry         the {@code BeanDefinitionRegistry} to make available to filters
     *                         as a {@link org.springframework.beans.factory.BeanFactory} if applicable
     * @return a list of instantiated and configured type filters
     * @see TypeFilter
     * @see AnnotationTypeFilter
     * @see AssignableTypeFilter
     * @see AspectJTypeFilter
     * @see RegexPatternTypeFilter
     * @see org.springframework.beans.factory.BeanClassLoaderAware
     * @see org.springframework.beans.factory.BeanFactoryAware
     * @see org.springframework.context.EnvironmentAware
     * @see org.springframework.context.ResourceLoaderAware
     */
    public static List<TypeFilter> createTypeFiltersFor(AnnotationAttributes filterAttributes, Environment environment,
                                                        ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {

        List<TypeFilter> typeFilters = new ArrayList<>();
        FilterType filterType = filterAttributes.getEnum("type");

        for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable(Annotation.class, filterClass,
                            "@ComponentScan ANNOTATION type filter requires an annotation type");
                    @SuppressWarnings("unchecked")
                    Class<Annotation> annotationType = (Class<Annotation>) filterClass;
                    typeFilters.add(new AnnotationTypeFilter(annotationType));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable(TypeFilter.class, filterClass,
                            "@ComponentScan CUSTOM type filter requires a TypeFilter implementation");
                    TypeFilter filter = ParserStrategyUtils.instantiateClass(filterClass, TypeFilter.class,
                            environment, resourceLoader, registry);
                    typeFilters.add(filter);
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with Class value: " + filterType);
            }
        }

        for (String expression : filterAttributes.getStringArray("pattern")) {
            switch (filterType) {
                case ASPECTJ:
                    typeFilters.add(new AspectJTypeFilter(expression, resourceLoader.getClassLoader()));
                    break;
                case REGEX:
                    typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(expression)));
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with String pattern: " + filterType);
            }
        }

        return typeFilters;
    }

}