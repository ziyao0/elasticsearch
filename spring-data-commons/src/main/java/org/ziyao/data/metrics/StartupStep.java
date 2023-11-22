package org.ziyao.data.metrics;

import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/**
 * @author ziyao zhang
 * @since 2023/11/20
 */
public interface StartupStep {
    String getName();

    long getId();

    @Nullable
    Long getParentId();

    StartupStep tag(String key, String value);

    StartupStep tag(String key, Supplier<String> value);

    Tags getTags();

    void end();

    public interface Tag {
        String getKey();

        String getValue();
    }

    public interface Tags extends Iterable<Tag> {
    }
}
