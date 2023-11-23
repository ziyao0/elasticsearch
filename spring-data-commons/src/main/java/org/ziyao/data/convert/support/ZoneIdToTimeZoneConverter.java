package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class ZoneIdToTimeZoneConverter implements Converter<ZoneId, TimeZone> {

    @Override
    public TimeZone convert(ZoneId source) {
        return TimeZone.getTimeZone(source);
    }

}
