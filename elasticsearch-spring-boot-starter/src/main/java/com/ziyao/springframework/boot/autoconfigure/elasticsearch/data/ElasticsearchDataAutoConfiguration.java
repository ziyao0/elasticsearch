package com.ziyao.springframework.boot.autoconfigure.elasticsearch.data;

import com.ziyao.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.ziyao.data.elasticsearch.client.elc.ElasticsearchTemplate;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@Configuration
@ConditionalOnClass({ElasticsearchTemplate.class})
@Import({ElasticsearchDataConfiguration.BaseConfiguration.class,
        ElasticsearchDataConfiguration.JavaClientConfiguration.class})
@AutoConfigureAfter({ElasticsearchClientAutoConfiguration.class})
public class ElasticsearchDataAutoConfiguration {

}
