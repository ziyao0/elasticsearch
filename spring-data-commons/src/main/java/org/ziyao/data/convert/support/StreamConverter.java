package org.ziyao.data.convert.support;

import org.ziyao.data.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
class StreamConverter implements ConditionalGenericConverter {

    private static final TypeDescriptor STREAM_TYPE = TypeDescriptor.valueOf(Stream.class);

    private static final Set<ConvertiblePair> CONVERTIBLE_TYPES = createConvertibleTypes();

    private final ConversionService conversionService;


    public StreamConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return CONVERTIBLE_TYPES;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(STREAM_TYPE)) {
            return matchesFromStream(sourceType.getElementTypeDescriptor(), targetType);
        }
        if (targetType.isAssignableTo(STREAM_TYPE)) {
            return matchesToStream(targetType.getElementTypeDescriptor(), sourceType);
        }
        return false;
    }

    /**
     * Validate that a {@link Collection} of the elements held within the stream can be
     * converted to the specified {@code targetType}.
     * @param elementType the type of the stream elements
     * @param targetType the type to convert to
     */
    public boolean matchesFromStream(@Nullable TypeDescriptor elementType, TypeDescriptor targetType) {
        TypeDescriptor collectionOfElement = TypeDescriptor.collection(Collection.class, elementType);
        return this.conversionService.canConvert(collectionOfElement, targetType);
    }

    /**
     * Validate that the specified {@code sourceType} can be converted to a {@link Collection} of
     * the type of the stream elements.
     * @param elementType the type of the stream elements
     * @param sourceType the type to convert from
     */
    public boolean matchesToStream(@Nullable TypeDescriptor elementType, TypeDescriptor sourceType) {
        TypeDescriptor collectionOfElement = TypeDescriptor.collection(Collection.class, elementType);
        return this.conversionService.canConvert(sourceType, collectionOfElement);
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(STREAM_TYPE)) {
            return convertFromStream((Stream<?>) source, sourceType, targetType);
        }
        if (targetType.isAssignableTo(STREAM_TYPE)) {
            return convertToStream(source, sourceType, targetType);
        }
        // Should not happen
        throw new IllegalStateException("Unexpected source/target types");
    }

    @Nullable
    private Object convertFromStream(@Nullable Stream<?> source, TypeDescriptor streamType, TypeDescriptor targetType) {
        List<Object> content = (source != null ? source.collect(Collectors.<Object>toList()) : Collections.emptyList());
        TypeDescriptor listType = TypeDescriptor.collection(List.class, streamType.getElementTypeDescriptor());
        return this.conversionService.convert(content, listType, targetType);
    }

    private Object convertToStream(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor streamType) {
        TypeDescriptor targetCollection = TypeDescriptor.collection(List.class, streamType.getElementTypeDescriptor());
        List<?> target = (List<?>) this.conversionService.convert(source, sourceType, targetCollection);
        if (target == null) {
            target = Collections.emptyList();
        }
        return target.stream();
    }


    private static Set<ConvertiblePair> createConvertibleTypes() {
        Set<ConvertiblePair> convertiblePairs = new HashSet<>();
        convertiblePairs.add(new ConvertiblePair(Stream.class, Collection.class));
        convertiblePairs.add(new ConvertiblePair(Stream.class, Object[].class));
        convertiblePairs.add(new ConvertiblePair(Collection.class, Stream.class));
        convertiblePairs.add(new ConvertiblePair(Object[].class, Stream.class));
        return convertiblePairs;
    }

}
