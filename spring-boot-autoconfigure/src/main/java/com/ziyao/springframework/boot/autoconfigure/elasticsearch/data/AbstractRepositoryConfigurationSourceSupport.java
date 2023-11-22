package com.ziyao.springframework.boot.autoconfigure.elasticsearch.data;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.ziyao.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.ziyao.data.repository.config.BootstrapMode;
import org.ziyao.data.repository.config.RepositoryConfigurationDelegate;
import org.ziyao.data.repository.config.RepositoryConfigurationExtension;
import org.ziyao.data.util.Streamable;

import java.lang.annotation.Annotation;

/**
 * @author ziyao
 * @since 2023/4/23
 */
public abstract class AbstractRepositoryConfigurationSourceSupport
        implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private BeanFactory beanFactory;

    private Environment environment;


    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
                                        BeanNameGenerator importBeanNameGenerator) {
        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(
                getConfigurationSource(registry, importBeanNameGenerator), this.resourceLoader, this.environment);
        delegate.registerRepositoriesIn(registry, getRepositoryConfigurationExtension());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerBeanDefinitions(importingClassMetadata, registry, null);
    }

    private AnnotationRepositoryConfigurationSource getConfigurationSource(BeanDefinitionRegistry registry,
                                                                           BeanNameGenerator importBeanNameGenerator) {
//        AnnotationMetadata metadata = AnnotationMetadata.introspect(getConfiguration());
        StandardAnnotationMetadata metadata = new StandardAnnotationMetadata(getConfiguration(), true);
        return new AutoConfiguredAnnotationRepositoryConfigurationSource(metadata, getAnnotation(), this.resourceLoader,
                this.environment, registry, importBeanNameGenerator) {
        };
    }

    protected Streamable<String> getBasePackages() {
        return Streamable.of(AutoConfigurationPackages.get(this.beanFactory));
    }

    /**
     * The Spring Data annotation used to enable the particular repository support.
     *
     * @return the annotation class
     */
    protected abstract Class<? extends Annotation> getAnnotation();

    /**
     * The configuration class that will be used by Spring Boot as a template.
     *
     * @return the configuration class
     */
    protected abstract Class<?> getConfiguration();

    /**
     * The {@link RepositoryConfigurationExtension} for the particular repository support.
     *
     * @return the repository configuration extension
     */
    protected abstract RepositoryConfigurationExtension getRepositoryConfigurationExtension();

    /**
     * The {@link BootstrapMode} for the particular repository support. Defaults to
     * {@link BootstrapMode#DEFAULT}.
     *
     * @return the bootstrap mode
     */
    protected BootstrapMode getBootstrapMode() {
        return BootstrapMode.DEFAULT;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * An auto-configured {@link AnnotationRepositoryConfigurationSource}.
     */
    private class AutoConfiguredAnnotationRepositoryConfigurationSource
            extends AnnotationRepositoryConfigurationSource {

        AutoConfiguredAnnotationRepositoryConfigurationSource(AnnotationMetadata metadata,
                                                              Class<? extends Annotation> annotation, ResourceLoader resourceLoader, Environment environment,
                                                              BeanDefinitionRegistry registry, BeanNameGenerator generator) {
            super(metadata, annotation, resourceLoader, environment, registry, generator);
        }

        @Override
        public Streamable<String> getBasePackages() {
            return AbstractRepositoryConfigurationSourceSupport.this.getBasePackages();
        }

        @Override
        public BootstrapMode getBootstrapMode() {
            return AbstractRepositoryConfigurationSourceSupport.this.getBootstrapMode();
        }

    }

}

