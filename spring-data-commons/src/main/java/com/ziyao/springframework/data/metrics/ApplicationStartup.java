package com.ziyao.springframework.data.metrics;

/**
 * @author ziyao zhang
 * @since 2023/11/20
 */
public interface ApplicationStartup {
    ApplicationStartup DEFAULT = new DefaultApplicationStartup();

    StartupStep start(String name);
}
