package org.ziyao.data.convert.support;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.core.convert.TypeDescriptor;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
@SuppressWarnings("serial")
public class ConversionFailedException extends ConversionException {

    @Nullable
    private final TypeDescriptor sourceType;

    private final TypeDescriptor targetType;

    @Nullable
    private final Object value;


    /**
     * Create a new conversion exception.
     *
     * @param sourceType the value's original type
     * @param targetType the value's target type
     * @param value      the value we tried to convert
     * @param cause      the cause of the conversion failure
     */
    public ConversionFailedException(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType,
                                     @Nullable Object value, Throwable cause) {

        super("Failed to convert from type [" + sourceType + "] to type [" + targetType +
                "] for value '" + ObjectUtils.nullSafeToString(value) + "'", cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.value = value;
    }


    /**
     * Return the source type we tried to convert the value from.
     */
    @Nullable
    public TypeDescriptor getSourceType() {
        return this.sourceType;
    }

    /**
     * Return the target type we tried to convert the value to.
     */
    public TypeDescriptor getTargetType() {
        return this.targetType;
    }

    /**
     * Return the offending value.
     */
    @Nullable
    public Object getValue() {
        return this.value;
    }

}
