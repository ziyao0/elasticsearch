package org.ziyao.data.convert.support;

import org.springframework.lang.Nullable;
import org.ziyao.data.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class ObjectToOptionalConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;


    public ObjectToOptionalConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> convertibleTypes = new LinkedHashSet<>(4);
        convertibleTypes.add(new ConvertiblePair(Collection.class, Optional.class));
        convertibleTypes.add(new ConvertiblePair(Object[].class, Optional.class));
        convertibleTypes.add(new ConvertiblePair(Object.class, Optional.class));
        return convertibleTypes;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.getResolvableType().hasGenerics()) {
            return this.conversionService.canConvert(sourceType, new ObjectToOptionalConverter.GenericTypeDescriptor(targetType));
        }
        else {
            return true;
        }
    }

    @Override
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return Optional.empty();
        }
        else if (source instanceof Optional) {
            return source;
        }
        else if (targetType.getResolvableType().hasGenerics()) {
            Object target = this.conversionService.convert(source, sourceType, new ObjectToOptionalConverter.GenericTypeDescriptor(targetType));
            if (target == null || (target.getClass().isArray() && Array.getLength(target) == 0) ||
                    (target instanceof Collection && ((Collection<?>) target).isEmpty())) {
                return Optional.empty();
            }
            return Optional.of(target);
        }
        else {
            return Optional.of(source);
        }
    }


    @SuppressWarnings("serial")
    private static class GenericTypeDescriptor extends TypeDescriptor {

        public GenericTypeDescriptor(TypeDescriptor typeDescriptor) {
            super(typeDescriptor.getResolvableType().getGeneric(), null, typeDescriptor.getAnnotations());
        }
    }

}
