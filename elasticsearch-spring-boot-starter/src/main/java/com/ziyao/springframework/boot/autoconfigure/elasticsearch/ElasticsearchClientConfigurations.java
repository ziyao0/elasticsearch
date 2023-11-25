package com.ziyao.springframework.boot.autoconfigure.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.SimpleJsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ziyao.data.elasticsearch.client.elc.ElasticsearchClients;

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


//    @ConditionalOnMissingBean(JsonpMapper.class)
//    @ConditionalOnBean(Jsonb.class)
//    @Configuration
//    static class JsonbJsonpMapperConfiguration {
//
//        @Bean
//        JsonbJsonpMapper jsonbJsonpMapper(Jsonb jsonb) {
//            return new JsonbJsonpMapper(JsonProvider.provider(), jsonb);
//        }
//
//    }

    @ConditionalOnMissingBean(JsonpMapper.class)
    @Configuration
    static class SimpleJsonpMapperConfiguration {

        @Bean
        SimpleJsonpMapper simpleJsonpMapper() {
            return new SimpleJsonpMapper();
        }

    }

    // TODO: 2023/11/25 查看是否影响操作 
    @Configuration
//    @ConditionalOnBean(ElasticsearchTransport.class)
    static class ElasticsearchClientConfiguration {

        @Bean
        @ConditionalOnMissingBean
        ElasticsearchClient elasticsearchClient(RestClient restClient) {
            return ElasticsearchClients.createImperative(restClient, new RestClientOptions(RequestOptions.DEFAULT));
        }

    }

}
