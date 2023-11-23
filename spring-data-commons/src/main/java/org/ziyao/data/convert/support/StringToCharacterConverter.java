package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;
import org.springframework.lang.Nullable;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class StringToCharacterConverter implements Converter<String, Character> {

    @Override
    @Nullable
    public Character convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        if (source.length() > 1) {
            throw new IllegalArgumentException(
                    "Can only convert a [String] with length of 1 to a [Character]; string value '" + source + "'  has length of " + source.length());
        }
        return source.charAt(0);
    }

}
