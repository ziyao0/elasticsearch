package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;

/**
 *
 * @author ziyao zhang
 * @since 2023/11/23
 */final class ObjectToStringConverter implements Converter<Object, String> {

    @Override
    public String convert(Object source) {
        return source.toString();
    }

}

