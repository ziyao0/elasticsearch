package org.ziyao.data.convert.support;

import org.springframework.core.CollectionFactory;
import org.ziyao.data.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class ObjectToCollectionConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;


    public ObjectToCollectionConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, Collection.class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType, targetType.getElementTypeDescriptor(), this.conversionService);
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        TypeDescriptor elementDesc = targetType.getElementTypeDescriptor();
        Collection<Object> target = CollectionFactory.createCollection(targetType.getType(),
                (elementDesc != null ? elementDesc.getType() : null), 1);

        if (elementDesc == null || elementDesc.isCollection()) {
            target.add(source);
        }
        else {
            Object singleElement = this.conversionService.convert(source, sourceType, elementDesc);
            target.add(singleElement);
        }
        return target;
    }

}
