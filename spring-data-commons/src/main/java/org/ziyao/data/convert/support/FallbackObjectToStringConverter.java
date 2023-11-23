package org.ziyao.data.convert.support;

import org.springframework.lang.Nullable;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class FallbackObjectToStringConverter implements ConditionalGenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, String.class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> sourceClass = sourceType.getObjectType();
        if (String.class == sourceClass) {
            // no conversion required
            return false;
        }
        return (CharSequence.class.isAssignableFrom(sourceClass) ||
                StringWriter.class.isAssignableFrom(sourceClass) ||
                org.ziyao.data.convert.support.ObjectToObjectConverter.hasConversionMethodOrConstructor(sourceClass, String.class));
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return (source != null ? source.toString() : null);
    }

}
