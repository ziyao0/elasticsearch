package com.ziyao.springboot.autoconfigure.elasticsearch.data;

import com.ziyao.springframework.data.elasticsearch.repository.config.ElasticsearchRepositoryConfigExtension;
import com.ziyao.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import com.ziyao.springframework.data.repository.config.RepositoryConfigurationExtension;

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

