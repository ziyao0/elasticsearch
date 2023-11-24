package com.ziyao.springframework.boot.autoconfigure.elasticsearch.data;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.ziyao.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@ConditionalOnClass(ElasticsearchClient.class)
//@Import({ElasticsearchTransportConfiguration.class, ElasticsearchClientConfiguration.class})
@AutoConfigureAfter({JacksonAutoConfiguration.class, JsonbAutoConfiguration.class,
        ElasticsearchRestClientAutoConfiguration.class})
public class ElasticsearchClientAutoConfiguration {
}
