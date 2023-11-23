package org.ziyao.data.metrics;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * @author ziyao zhang
 * @since 2023/11/20
 */
class DefaultApplicationStartup implements ApplicationStartup {
    private static final DefaultStartupStep DEFAULT_STARTUP_STEP = new DefaultStartupStep();

    DefaultApplicationStartup() {
    }

    public DefaultStartupStep start(String name) {
        return DEFAULT_STARTUP_STEP;
    }

    static class DefaultStartupStep implements StartupStep {
        private final DefaultTags TAGS = new DefaultTags();

        DefaultStartupStep() {
        }

        public String getName() {
            return "default";
        }

        public long getId() {
            return 0L;
        }

        public Long getParentId() {
            return null;
        }

        public Tags getTags() {
            return this.TAGS;
        }

        public StartupStep tag(String key, String value) {
            return this;
        }

        public StartupStep tag(String key, Supplier<String> value) {
            return this;
        }

        public void end() {
        }

        static class DefaultTags implements Tags {
            DefaultTags() {
            }

            public Iterator<Tag> iterator() {
                return Collections.emptyIterator();
            }
        }
    }
}
