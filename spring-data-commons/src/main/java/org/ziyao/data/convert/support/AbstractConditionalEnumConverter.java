package org.ziyao.data.convert.support;


import org.springframework.util.ClassUtils;
import org.ziyao.data.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalConverter;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
abstract class AbstractConditionalEnumConverter implements ConditionalConverter {

    private final ConversionService conversionService;


    protected AbstractConditionalEnumConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        for (Class<?> interfaceType : ClassUtils.getAllInterfacesForClassAsSet(sourceType.getType())) {
            if (this.conversionService.canConvert(TypeDescriptor.valueOf(interfaceType), targetType)) {
                return false;
            }
        }
        return true;
    }

}