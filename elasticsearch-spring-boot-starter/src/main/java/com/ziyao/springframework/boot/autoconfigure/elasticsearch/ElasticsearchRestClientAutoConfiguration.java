package com.ziyao.springframework.boot.autoconfigure.elasticsearch;

import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@Configuration
@ConditionalOnClass(RestClientBuilder.class)
@EnableConfigurationProperties({ElasticsearchProperties.class, ElasticsearchRestClientProperties.class})
@Import({ElasticsearchRestClientConfigurations.RestClientBuilderConfiguration.class,
        ElasticsearchRestClientConfigurations.RestClientConfiguration.class})
public class ElasticsearchRestClientAutoConfiguration {

}
