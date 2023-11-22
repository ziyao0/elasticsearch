package com.ziyao.springframework.boot.autoconfigure.elasticsearch.data;

import org.ziyao.data.elasticsearch.repository.config.ElasticsearchRepositoryConfigExtension;
import org.ziyao.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.ziyao.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * @author ziyao
 * @since 2023/4/23
 */
class ElasticsearchRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableElasticsearchRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return EnableElasticsearchRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ElasticsearchRepositoryConfigExtension();
    }

    @EnableElasticsearchRepositories
    private static class EnableElasticsearchRepositoriesConfiguration {

    }

}

