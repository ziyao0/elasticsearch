package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class ZonedDateTimeToCalendarConverter implements Converter<ZonedDateTime, Calendar> {

    @Override
    public Calendar convert(ZonedDateTime source) {
        return GregorianCalendar.from(source);
    }

}