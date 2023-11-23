package org.ziyao.data.convert.support;

import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;
import org.ziyao.data.convert.converter.Converter;
import org.ziyao.data.convert.converter.ConverterFactory;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumberConverterFactory.StringToNumber<>(targetType);
    }


    private static final class StringToNumber<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        @Nullable
        public T convert(String source) {
            if (source.isEmpty()) {
                return null;
            }
            return NumberUtils.parseNumber(source, this.targetType);
        }
    }

}