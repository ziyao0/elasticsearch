package org.ziyao.data.convert.support;

import org.ziyao.data.convert.ConversionService;
import org.ziyao.data.convert.converter.Converter;


/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class EnumToStringConverter extends AbstractConditionalEnumConverter implements Converter<Enum<?>, String> {

    public EnumToStringConverter(ConversionService conversionService) {
        super(conversionService);
    }

    @Override
    public String convert(Enum<?> source) {
        return source.name();
    }

}
