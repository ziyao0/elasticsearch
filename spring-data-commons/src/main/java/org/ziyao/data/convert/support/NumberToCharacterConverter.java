package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class NumberToCharacterConverter implements Converter<Number, Character> {

    @Override
    public Character convert(Number source) {
        return (char) source.shortValue();
    }

}
