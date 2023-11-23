package org.ziyao.data.convert.converter;


import org.springframework.core.convert.TypeDescriptor;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
public interface ConditionalConverter {

    /**
     * Should the conversion from {@code sourceType} to {@code targetType} currently under
     * consideration be selected?
     *
     * @param sourceType the type descriptor of the field we are converting from
     * @param targetType the type descriptor of the field we are converting to
     * @return true if conversion should be performed, false otherwise
     */
    boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);

}