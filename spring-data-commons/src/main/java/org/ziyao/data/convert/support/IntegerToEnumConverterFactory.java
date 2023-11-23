package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;
import org.ziyao.data.convert.converter.ConverterFactory;


/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
@SuppressWarnings({"rawtypes", "unchecked"})
final class IntegerToEnumConverterFactory implements ConverterFactory<Integer, Enum> {

    @Override
    public <T extends Enum> Converter<Integer, T> getConverter(Class<T> targetType) {
        return new IntegerToEnumConverterFactory.IntegerToEnum(ConversionUtils.getEnumType(targetType));
    }


    private static class IntegerToEnum<T extends Enum> implements Converter<Integer, T> {

        private final Class<T> enumType;

        public IntegerToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(Integer source) {
            return this.enumType.getEnumConstants()[source];
        }
    }

}