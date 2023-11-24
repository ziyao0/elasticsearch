package com.ziyao.springframework.boot.autoconfigure.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.SimpleJsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ziyao
 * @since 2023/4/23
 */
class ElasticsearchClientConfigurations {

    @ConditionalOnMissingBean(JsonpMapper.class)
    @ConditionalOnBean(ObjectMapper.class)
    @Configuration
    static class JacksonJsonpMapperConfiguration {

        @Bean
        JacksonJsonpMapper jacksonJsonpMapper() {
            return new JacksonJsonpMapper();
        }

    }

    @ConditionalOnMissingBean(JsonpMapper.class)
    @Configuration
    static class SimpleJsonpMapperConfiguration {

        @Bean
        SimpleJsonpMapper simpleJsonpMapper() {
            return new SimpleJsonpMapper();
        }

    }

    @Configuration
    @ConditionalOnBean(ElasticsearchTransport.class)
    static class ElasticsearchClientConfiguration {

        @Bean
        @ConditionalOnMissingBean
        ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
            return new ElasticsearchClient(transport);
        }

    }

}
