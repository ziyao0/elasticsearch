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
     * Whether to enable socket keep alive between client and Elasticsearch.
     */
    private boolean socketKeepAlive = false;

    /**
     * Prefix added to the path of every request sent to Elasticsearch.
     */
    private String pathPrefix;

    private final Restclient restclient = new Restclient();

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

    public boolean isSocketKeepAlive() {
        return this.socketKeepAlive;
    }

    public void setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

    public String getPathPrefix() {
        return this.pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public Restclient getRestclient() {
        return this.restclient;
    }

    public static class Restclient {

        private final Sniffer sniffer = new Sniffer();

        public Sniffer getSniffer() {
            return this.sniffer;
        }

        public static class Sniffer {

            /**
             * Interval between consecutive ordinary sniff executions.
             */
            private Duration interval = Duration.ofMinutes(5);

            /**
             * Delay of a sniff execution scheduled after a failure.
             */
            private Duration delayAfterFailure = Duration.ofMinutes(1);

            public Duration getInterval() {
                return this.interval;
            }

            public void setInterval(Duration interval) {
                this.interval = interval;
            }

            public Duration getDelayAfterFailure() {
                return this.delayAfterFailure;
            }

            public void setDelayAfterFailure(Duration delayAfterFailure) {
                this.delayAfterFailure = delayAfterFailure;
            }

        }

    }

}
