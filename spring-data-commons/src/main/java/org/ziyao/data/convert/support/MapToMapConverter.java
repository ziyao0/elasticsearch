package org.ziyao.data.convert.support;

import org.springframework.core.CollectionFactory;
import org.ziyao.data.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.ziyao.data.convert.converter.ConditionalGenericConverter;

import org.springframework.lang.Nullable;

import java.util.*;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
final class MapToMapConverter implements ConditionalGenericConverter {

    private final ConversionService conversionService;


    public MapToMapConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Map.class, Map.class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return canConvertKey(sourceType, targetType) && canConvertValue(sourceType, targetType);
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<Object, Object> sourceMap = (Map<Object, Object>) source;

        // Shortcut if possible...
        boolean copyRequired = !targetType.getType().isInstance(source);
        if (!copyRequired && sourceMap.isEmpty()) {
            return sourceMap;
        }
        TypeDescriptor keyDesc = targetType.getMapKeyTypeDescriptor();
        TypeDescriptor valueDesc = targetType.getMapValueTypeDescriptor();

        List<MapToMapConverter.MapEntry> targetEntries = new ArrayList<>(sourceMap.size());
        for (Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
            Object sourceKey = entry.getKey();
            Object sourceValue = entry.getValue();
            Object targetKey = convertKey(sourceKey, sourceType, keyDesc);
            Object targetValue = convertValue(sourceValue, sourceType, valueDesc);
            targetEntries.add(new MapToMapConverter.MapEntry(targetKey, targetValue));
            if (sourceKey != targetKey || sourceValue != targetValue) {
                copyRequired = true;
            }
        }
        if (!copyRequired) {
            return sourceMap;
        }

        Map<Object, Object> targetMap = CollectionFactory.createMap(targetType.getType(),
                (keyDesc != null ? keyDesc.getType() : null), sourceMap.size());

        for (MapToMapConverter.MapEntry entry : targetEntries) {
            entry.addToMap(targetMap);
        }
        return targetMap;
    }


    // internal helpers

    private boolean canConvertKey(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getMapKeyTypeDescriptor(),
                targetType.getMapKeyTypeDescriptor(), this.conversionService);
    }

    private boolean canConvertValue(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return ConversionUtils.canConvertElements(sourceType.getMapValueTypeDescriptor(),
                targetType.getMapValueTypeDescriptor(), this.conversionService);
    }

    @Nullable
    private Object convertKey(Object sourceKey, TypeDescriptor sourceType, @Nullable TypeDescriptor targetType) {
        if (targetType == null) {
            return sourceKey;
        }
        return this.conversionService.convert(sourceKey, sourceType.getMapKeyTypeDescriptor(sourceKey), targetType);
    }

    @Nullable
    private Object convertValue(Object sourceValue, TypeDescriptor sourceType, @Nullable TypeDescriptor targetType) {
        if (targetType == null) {
            return sourceValue;
        }
        return this.conversionService.convert(sourceValue, sourceType.getMapValueTypeDescriptor(sourceValue), targetType);
    }


    private static class MapEntry {

        @Nullable
        private final Object key;

        @Nullable
        private final Object value;

        public MapEntry(@Nullable Object key, @Nullable Object value) {
            this.key = key;
            this.value = value;
        }

        public void addToMap(Map<Object, Object> map) {
            map.put(this.key, this.value);
        }
    }

}
