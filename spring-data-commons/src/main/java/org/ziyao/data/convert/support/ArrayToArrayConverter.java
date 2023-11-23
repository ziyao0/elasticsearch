package org.ziyao.data.convert.support;

import org.ziyao.data.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class ArrayToArrayConverter implements ConditionalGenericConverter {

    private final CollectionToArrayConverter helperConverter;

    private final ConversionService conversionService;


    public ArrayToArrayConverter(ConversionService conversionService) {
        this.helperConverter = new CollectionToArrayConverter(conversionService);
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object[].class, Object[].class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.helperConverter.matches(sourceType, targetType);
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (this.conversionService instanceof GenericConversionService) {
            TypeDescriptor targetElement = targetType.getElementTypeDescriptor();
            if (targetElement != null &&
                    ((GenericConversionService) this.conversionService).canBypassConvert(
                            sourceType.getElementTypeDescriptor(), targetElement)) {
                return source;
            }
        }
        List<Object> sourceList = Arrays.asList(ObjectUtils.toObjectArray(source));
        return this.helperConverter.convert(sourceList, sourceType, targetType);
    }

}
