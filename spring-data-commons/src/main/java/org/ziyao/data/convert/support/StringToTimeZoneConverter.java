package org.ziyao.data.convert.support;

import org.springframework.util.StringUtils;
import org.ziyao.data.convert.converter.Converter;

import java.util.TimeZone;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
class StringToTimeZoneConverter implements Converter<String, TimeZone> {

    @Override
    public TimeZone convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return StringUtils.parseTimeZoneString(source);
    }

}
