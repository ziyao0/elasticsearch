package com.ziyao.springframework.boot.autoconfigure.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;

/**
 * @author ziyao
 * @since 2023/4/23
 */
class ElasticsearchRestClientConfigurations {

    @Configuration
    @ConditionalOnMissingBean(RestClientBuilder.class)
    static class RestClientBuilderConfiguration {

        private final ConsolidatedProperties properties;

        RestClientBuilderConfiguration(ElasticsearchProperties properties) {
            this.properties = new ConsolidatedProperties(properties);
        }

        @Bean
        RestClientBuilderCustomizer defaultRestClientBuilderCustomizer() {
            return new DefaultRestClientBuilderCustomizer(this.properties);
        }

        @Bean
        RestClientBuilder elasticsearchRestClientBuilder(
                ObjectProvider<RestClientBuilderCustomizer> builderCustomizers) {
            HttpHost[] hosts = this.properties.getUris().stream().map(this::createHttpHost).toArray(HttpHost[]::new);
            RestClientBuilder builder = RestClient.builder(hosts);
            builder.setHttpClientConfigCallback((httpClientBuilder) -> {
                builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(httpClientBuilder));
                return httpClientBuilder;
            });
            builder.setRequestConfigCallback((requestConfigBuilder) -> {
                builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(requestConfigBuilder));
                return requestConfigBuilder;
            });
            if (this.properties.getPathPrefix() != null) {
                builder.setPathPrefix(this.properties.properties.getPathPrefix());
            }
            builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
            return builder;
        }

        private HttpHost createHttpHost(String uri) {
            try {
                return createHttpHost(URI.create(uri));
            } catch (IllegalArgumentException ex) {
                return HttpHost.create(uri);
            }
        }

        private HttpHost createHttpHost(URI uri) {
            if (!StringUtils.hasLength(uri.getUserInfo())) {
                return HttpHost.create(uri.toString());
            }
            try {
                return HttpHost.create(new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(),
                        uri.getQuery(), uri.getFragment())
                        .toString());
            } catch (URISyntaxException ex) {
                throw new IllegalStateException(ex);
            }
        }

    }





    @Configuration
    @ConditionalOnMissingClass("org.elasticsearch.client.RestHighLevelClient")
    @ConditionalOnMissingBean(RestClient.class)
    static class RestClientConfiguration {

        @Bean
        RestClient elasticsearchRestClient(RestClientBuilder restClientBuilder) {
            return restClientBuilder.build();
        }

    }


    static class DefaultRestClientBuilderCustomizer implements RestClientBuilderCustomizer {

        private static final PropertyMapper map = PropertyMapper.get();

        private final ConsolidatedProperties properties;

        DefaultRestClientBuilderCustomizer(ConsolidatedProperties properties) {
            this.properties = properties;
        }

        @Override
        public void customize(RestClientBuilder builder) {
        }

        @Override
        public void customize(HttpAsyncClientBuilder builder) {
            builder.setDefaultCredentialsProvider(new PropertiesCredentialsProvider(this.properties));
        }

        @Override
        public void customize(RequestConfig.Builder builder) {
            map.from(this.properties::getConnectionTimeout)
                    .whenNonNull()
                    .asInt(Duration::toMillis)
                    .to(builder::setConnectTimeout);
            map.from(this.properties::getSocketTimeout)
                    .whenNonNull()
                    .asInt(Duration::toMillis)
                    .to(builder::setSocketTimeout);
        }

    }

    private static class PropertiesCredentialsProvider extends BasicCredentialsProvider {

        PropertiesCredentialsProvider(ConsolidatedProperties properties) {
            if (StringUtils.hasText(properties.getUsername())) {
                Credentials credentials = new UsernamePasswordCredentials(properties.getUsername(),
                        properties.getPassword());
                setCredentials(AuthScope.ANY, credentials);
            }
            properties.getUris()
                    .stream()
                    .map(this::toUri)
                    .filter(this::hasUserInfo)
                    .forEach(this::addUserInfoCredentials);
        }

        private URI toUri(String uri) {
            try {
                return URI.create(uri);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }

        private boolean hasUserInfo(URI uri) {
            return uri != null && StringUtils.hasLength(uri.getUserInfo());
        }

        private void addUserInfoCredentials(URI uri) {
            AuthScope authScope = new AuthScope(uri.getHost(), uri.getPort());
            Credentials credentials = createUserInfoCredentials(uri.getUserInfo());
            setCredentials(authScope, credentials);
        }

        private Credentials createUserInfoCredentials(String userInfo) {
            int delimiter = userInfo.indexOf(":");
            if (delimiter == -1) {
                return new UsernamePasswordCredentials(userInfo, null);
            }
            String username = userInfo.substring(0, delimiter);
            String password = userInfo.substring(delimiter + 1);
            return new UsernamePasswordCredentials(username, password);
        }

    }

    private static final class ConsolidatedProperties {

        private final ElasticsearchProperties properties;


        private ConsolidatedProperties(ElasticsearchProperties properties) {
            this.properties = properties;
        }

        private List<String> getUris() {
            return this.properties.getUris();
        }

        private String getUsername() {
            return this.properties.getUsername();
        }

        private String getPassword() {
            return this.properties.getPassword();
        }

        private Duration getConnectionTimeout() {
            return this.properties.getConnectionTimeout();
        }

        private Duration getSocketTimeout() {
            return this.properties.getSocketTimeout();
        }

        private String getPathPrefix() {
            return this.properties.getPathPrefix();
        }

    }

}
