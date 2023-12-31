package org.ziyao.data.convert.support;

import org.ziyao.data.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class CollectionToStringConverter implements ConditionalGenericConverter {

    private static final String DELIMITER = ",";

    private final ConversionService conversionService;


    public CollectionToStringConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Collection.class, String.class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(
                sourceType.getElementTypeDescriptor(), targetType, this.conversionService);
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Collection<?> sourceCollection = (Collection<?>) source;
        if (sourceCollection.isEmpty()) {
            return "";
        }
        StringJoiner sj = new StringJoiner(DELIMITER);
        for (Object sourceElement : sourceCollection) {
            Object targetElement = this.conversionService.convert(
                    sourceElement, sourceType.elementTypeDescriptor(sourceElement), targetType);
            sj.add(String.valueOf(targetElement));
        }
        return sj.toString();
    }

}
