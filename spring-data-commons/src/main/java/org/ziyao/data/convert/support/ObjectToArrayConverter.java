package org.ziyao.data.convert.support;

import org.ziyao.data.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class ObjectToArrayConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;


    public ObjectToArrayConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, Object[].class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType, targetType.getElementTypeDescriptor(),
                this.conversionService);
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
        Assert.state(targetElementType != null, "No target element type");
        Object target = Array.newInstance(targetElementType.getType(), 1);
        Object targetElement = this.conversionService.convert(source, sourceType, targetElementType);
        Array.set(target, 0, targetElement);
        return target;
    }

}
