package com.ziyao.springframework.boot.autoconfigure.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@ConfigurationProperties("spring.elasticsearch")
public class ElasticsearchProperties {

    /**
     * Comma-separated list of the Elasticsearch instances to use.
     */
    private List<String> uris = new ArrayList<>(Collections.singletonList("http://localhost:9200"));

    /**
     * Username for authentication with Elasticsearch.
     */
    private String username;

    /**
     * Password for authentication with Elasticsearch.
     */
    private String password;

    /**
     * Connection timeout used when communicating with Elasticsearch.
     */
    private Duration connectionTimeout = Duration.ofSeconds(1);

    /**
     * Socket timeout used when communicating with Elasticsearch.
     */
    private Duration socketTimeout = Duration.ofSeconds(30);

    /**
     * Prefix added to the path of every request sent to Elasticsearch.
     */
    private String pathPrefix;

    public List<String> getUris() {
        return this.uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(Duration socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getPathPrefix() {
        return this.pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

}
