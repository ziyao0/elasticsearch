package org.ziyao.data.convert.support;

import org.ziyao.data.convert.ConversionService;
import org.ziyao.data.convert.converter.Converter;


/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class EnumToIntegerConverter extends AbstractConditionalEnumConverter implements Converter<Enum<?>, Integer> {

    public EnumToIntegerConverter(ConversionService conversionService) {
        super(conversionService);
    }

    @Override
    public Integer convert(Enum<?> source) {
        return source.ordinal();
    }

}
