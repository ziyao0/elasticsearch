/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ziyao.data.mapping;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;

/**
 * A context object for lookups of values for {@link PersistentPropertyPaths} via a {@link PersistentPropertyAccessor}.
 * It allows to register functions to post-process the objects returned for a particular property, so that the
 * subsequent traversal would rather use the processed object. This is especially helpful if you need to traverse paths
 * that contain {@link Collection}s and {@link Map} that usually need indices and keys to reasonably traverse nested
 * properties.
 *
 * @author Oliver Drotbohm
 * @soundtrack 8-Bit Misfits - Crash Into Me (Dave Matthews Band cover)
 * @since 2.2
 */
public class TraversalContext {

    private Map<PersistentProperty<?>, Function<Object, Object>> handlers = new HashMap<>();

    /**
     * Registers a {@link Function} to post-process values for the given property.
     *
     * @param property must not be {@literal null}.
     * @param handler  must not be {@literal null}.
     * @return
     */
    public TraversalContext registerHandler(PersistentProperty<?> property, Function<Object, Object> handler) {

        Assert.notNull(property, "Property must not be null");
        Assert.notNull(handler, "Handler must not be null");

        handlers.put(property, handler);

        return this;
    }

    /**
     * Registers a {@link Function} to handle {@link Collection} values for the given property.
     *
     * @param property must not be {@literal null}.
     * @param handler  must not be {@literal null}.
     * @return
     */
    @SuppressWarnings("unchecked")
    public TraversalContext registerCollectionHandler(PersistentProperty<?> property,
                                                      Function<? super Collection<?>, Object> handler) {
        return registerHandler(property, Collection.class, (Function<Object, Object>) handler);
    }

    /**
     * Registers a {@link Function} to handle {@link List} values for the given property.
     *
     * @param property must not be {@literal null}.
     * @param handler  must not be {@literal null}.
     * @return
     */
    @SuppressWarnings("unchecked")
    public TraversalContext registerListHandler(PersistentProperty<?> property,
                                                Function<? super List<?>, Object> handler) {
        return registerHandler(property, List.class, (Function<Object, Object>) handler);
    }

    /**
     * Registers a {@link Function} to handle {@link Set} values for the given property.
     *
     * @param property must not be {@literal null}.
     * @param handler  must not be {@literal null}.
     * @return
     */
    @SuppressWarnings("unchecked")
    public TraversalContext registerSetHandler(PersistentProperty<?> property, Function<? super Set<?>, Object> handler) {
        return registerHandler(property, Set.class, (Function<Object, Object>) handler);
    }

    /**
     * Registers a {@link Function} to handle {@link Map} values for the given property.
     *
     * @param property must not be {@literal null}.
     * @param handler  must not be {@literal null}.
     * @return
     */
    @SuppressWarnings("unchecked")
    public TraversalContext registerMapHandler(PersistentProperty<?> property,
                                               Function<? super Map<?, ?>, Object> handler) {
        return registerHandler(property, Map.class, (Function<Object, Object>) handler);
    }

    /**
     * Registers the given {@link Function} to post-process values obtained for the given {@link PersistentProperty} for
     * the given type.
     *
     * @param <T>      the type of the value to handle.
     * @param property must not be {@literal null}.
     * @param type     must not be {@literal null}.
     * @param handler  must not be {@literal null}.
     * @return
     */
    public <T> TraversalContext registerHandler(PersistentProperty<?> property, Class<T> type,
                                                Function<? super T, Object> handler) {

        Assert.isTrue(type.isAssignableFrom(property.getType()), () -> String
                .format("Cannot register a property handler for %s on a property of type %s", type, property.getType()));

        Function<Object, T> caster = it -> type.cast(it);

        return registerHandler(property, caster.andThen(handler));
    }

    /**
     * Post-processes the value obtained for the given {@link PersistentProperty} using the registered handler.
     *
     * @param property must not be {@literal null}.
     * @param value    can be {@literal null}.
     * @return the post-processed value or the value itself if no handlers registered.
     */
    @Nullable
    public Object postProcess(PersistentProperty<?> property, @Nullable Object value) {

        Function<Object, Object> handler = handlers.get(property);

        return handler == null ? value : handler.apply(value);
    }
}
