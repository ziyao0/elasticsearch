package com.ziyao.springframework.boot.autoconfigure.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author ziyao
 * @since 2023/4/23
 */
@ConfigurationProperties(prefix = "spring.elasticsearch.restclient")
public class ElasticsearchRestClientProperties {

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

