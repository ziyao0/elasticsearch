package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;
import org.ziyao.data.convert.converter.ConverterFactory;
import org.springframework.util.NumberUtils;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class CharacterToNumberFactory implements ConverterFactory<Character, Number> {

    @Override
    public <T extends Number> Converter<Character, T> getConverter(Class<T> targetType) {
        return new CharacterToNumberFactory.CharacterToNumber<>(targetType);
    }

    private static final class CharacterToNumber<T extends Number> implements Converter<Character, T> {

        private final Class<T> targetType;

        public CharacterToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T convert(Character source) {
            return NumberUtils.convertNumberToTargetClass((short) source.charValue(), this.targetType);
        }
    }

}
