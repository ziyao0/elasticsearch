package com.ziyao.springboot.autoconfigure.elasticsearch.data;

import com.ziyao.springboot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import com.ziyao.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@Configuration(after = { ElasticsearchRestClientAutoConfiguration.class })
@ConditionalOnClass({ ElasticsearchRestTemplate.class })
@Import({ ElasticsearchDataConfiguration.BaseConfiguration.class,
        ElasticsearchDataConfiguration.RestClientConfiguration.class,
        ElasticsearchDataConfiguration.ReactiveRestClientConfiguration.class })
public class ElasticsearchDataAutoConfiguration {

}
