package com.ziyao.springframework.data.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.Assert;

/**
 * @author ziyao zhang
 * @since 2023/11/20
 */
public class FullyQualifiedAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    /**
     * A convenient constant for a default {@code FullyQualifiedAnnotationBeanNameGenerator}
     * instance, as used for configuration-level import purposes.
     * @since 5.2.11
     */
    public static final FullyQualifiedAnnotationBeanNameGenerator INSTANCE =
            new FullyQualifiedAnnotationBeanNameGenerator();


    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        return beanClassName;
    }

}
