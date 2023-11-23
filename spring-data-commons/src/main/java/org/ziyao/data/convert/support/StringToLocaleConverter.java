package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class StringToLocaleConverter implements Converter<String, Locale> {

    @Override
    @Nullable
    public Locale convert(String source) {
        return StringUtils.parseLocale(source);
    }

}
