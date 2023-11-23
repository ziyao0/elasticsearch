package org.ziyao.data.convert.support;

import org.springframework.util.StringUtils;
import org.ziyao.data.convert.converter.Converter;

import java.nio.charset.Charset;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
class StringToCharsetConverter implements Converter<String, Charset> {

    @Override
    public Charset convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return Charset.forName(source);
    }

}