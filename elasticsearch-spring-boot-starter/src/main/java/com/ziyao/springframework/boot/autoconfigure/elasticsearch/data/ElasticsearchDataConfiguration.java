package com.ziyao.springframework.boot.autoconfigure.elasticsearch.data;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ziyao.data.elasticsearch.annotations.Document;
import org.ziyao.data.elasticsearch.core.ElasticsearchOperations;
import org.ziyao.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.ziyao.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.ziyao.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.ziyao.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.ziyao.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

import java.util.Collections;

/**
 * @author ziyao
 * @since 2023/4/23
 */
abstract class ElasticsearchDataConfiguration {

    @Configuration
    static class BaseConfiguration {

        @Bean
        @ConditionalOnMissingBean
        ElasticsearchCustomConversions elasticsearchCustomConversions() {
            return new ElasticsearchCustomConversions(Collections.emptyList());
        }

        @Bean
        @ConditionalOnMissingBean
        SimpleElasticsearchMappingContext mappingContext(ApplicationContext applicationContext,
                                                         ElasticsearchCustomConversions elasticsearchCustomConversions) throws ClassNotFoundException {
            SimpleElasticsearchMappingContext mappingContext = new SimpleElasticsearchMappingContext();
            mappingContext.setInitialEntitySet(new EntityScanner(applicationContext).scan(Document.class));
            mappingContext.setSimpleTypeHolder(elasticsearchCustomConversions.getSimpleTypeHolder());
            return mappingContext;
        }

        @Bean
        @ConditionalOnMissingBean
        ElasticsearchConverter elasticsearchConverter(SimpleElasticsearchMappingContext mappingContext,
                                                      ElasticsearchCustomConversions elasticsearchCustomConversions) {
            MappingElasticsearchConverter converter = new MappingElasticsearchConverter(mappingContext);
            converter.setConversions(elasticsearchCustomConversions);
            return converter;
        }

    }

    @SuppressWarnings("deprecation")
    @Configuration
    @ConditionalOnClass(org.elasticsearch.client.RestHighLevelClient.class)
    static class RestClientConfiguration {

        @Bean
        @ConditionalOnMissingBean(value = ElasticsearchOperations.class, name = "elasticsearchTemplate")
        @ConditionalOnBean(org.elasticsearch.client.RestHighLevelClient.class)
        ElasticsearchRestTemplate elasticsearchTemplate(org.elasticsearch.client.RestHighLevelClient client,
                                                        ElasticsearchConverter converter) {
            return new ElasticsearchRestTemplate(client, converter);
        }

    }
}
