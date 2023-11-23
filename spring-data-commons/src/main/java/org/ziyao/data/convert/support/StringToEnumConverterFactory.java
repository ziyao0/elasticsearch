package org.ziyao.data.convert.support;

import org.ziyao.data.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.ziyao.data.convert.converter.ConverterFactory;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
@SuppressWarnings({"rawtypes", "unchecked"})
final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverterFactory.StringToEnum(ConversionUtils.getEnumType(targetType));
    }


    private static class StringToEnum<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        @Nullable
        public T convert(String source) {
            if (source.isEmpty()) {
                // It's an empty enum identifier: reset the enum value to null.
                return null;
            }
            return (T) Enum.valueOf(this.enumType, source.trim());
        }
    }

}
