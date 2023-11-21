package com.ziyao.springframework.boot.autoconfigure.elasticsearch.data;

import com.ziyao.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.ziyao.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactoryBean;
import org.elasticsearch.client.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@Configuration
@ConditionalOnClass({Client.class, ElasticsearchRepository.class})
@ConditionalOnProperty(prefix = "spring.data.elasticsearch.repositories", name = "enabled", havingValue = "true",
        matchIfMissing = true)
@ConditionalOnMissingBean(ElasticsearchRepositoryFactoryBean.class)
@Import(ElasticsearchRepositoriesRegistrar.class)
public class ElasticsearchRepositoriesAutoConfiguration {

}
