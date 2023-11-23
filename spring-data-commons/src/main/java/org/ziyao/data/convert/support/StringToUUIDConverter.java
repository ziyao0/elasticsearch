package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class StringToUUIDConverter implements Converter<String, UUID> {

    @Override
    @Nullable
    public UUID convert(String source) {
        return (StringUtils.hasText(source) ? UUID.fromString(source.trim()) : null);
    }

}
