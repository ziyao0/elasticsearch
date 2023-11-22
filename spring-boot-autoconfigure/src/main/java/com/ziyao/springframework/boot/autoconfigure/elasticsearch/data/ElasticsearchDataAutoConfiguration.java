package com.ziyao.springframework.boot.autoconfigure.elasticsearch.data;

import com.ziyao.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.ziyao.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@Configuration
@ConditionalOnClass({ElasticsearchRestTemplate.class})
@Import({ElasticsearchDataConfiguration.BaseConfiguration.class,
        ElasticsearchDataConfiguration.RestClientConfiguration.class})
@AutoConfigureAfter({ElasticsearchRestClientAutoConfiguration.class})
public class ElasticsearchDataAutoConfiguration {

}
