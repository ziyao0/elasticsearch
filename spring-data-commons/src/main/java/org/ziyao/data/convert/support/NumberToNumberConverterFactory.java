package org.ziyao.data.convert.support;

import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalConverter;
import org.ziyao.data.convert.converter.Converter;
import org.ziyao.data.convert.converter.ConverterFactory;
import org.springframework.util.NumberUtils;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class NumberToNumberConverterFactory implements ConverterFactory<Number, Number>, ConditionalConverter {

    @Override
    public <T extends Number> Converter<Number, T> getConverter(Class<T> targetType) {
        return new NumberToNumberConverterFactory.NumberToNumber<>(targetType);
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return !sourceType.equals(targetType);
    }


    private static final class NumberToNumber<T extends Number> implements Converter<Number, T> {

        private final Class<T> targetType;

        NumberToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T convert(Number source) {
            return NumberUtils.convertNumberToTargetClass(source, this.targetType);
        }
    }

}