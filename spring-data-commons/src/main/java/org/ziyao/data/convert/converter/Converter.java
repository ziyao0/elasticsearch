package org.ziyao.data.convert.converter;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
@FunctionalInterface
public interface Converter<S, T> {

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Nullable
    T convert(S source);

    default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
        Assert.notNull(after, "After Converter must not be null");
        return (S s) -> {
            T initialResult = convert(s);
            return (initialResult != null ? after.convert(initialResult) : null);
        };
    }
}
