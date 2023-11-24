package com.ziyao.springframework.boot.autoconfigure.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@Configuration
@ConditionalOnClass(ElasticsearchClient.class)
@Import({ElasticsearchClientConfigurations.ElasticsearchClientConfiguration.class})
@AutoConfigureAfter({JacksonAutoConfiguration.class, JsonbAutoConfiguration.class,
        ElasticsearchRestClientAutoConfiguration.class})
public class ElasticsearchClientAutoConfiguration {
}
