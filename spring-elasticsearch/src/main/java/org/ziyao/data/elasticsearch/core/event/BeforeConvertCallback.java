/*
 * Copyright 2020-2023 the original author or authors.
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
package org.ziyao.data.elasticsearch.core.event;

import org.ziyao.data.mapping.callback.EntityCallback;
import org.ziyao.data.elasticsearch.core.mapping.IndexCoordinates;

/**
 * Callback being invoked before a domain object is converted to be persisted.
 *
 * @author Peter-Josef Meisch
 * @author Roman Puchkovskiy
 * @since 4.0
 */
@FunctionalInterface
public interface BeforeConvertCallback<T> extends EntityCallback<T> {

    /**
     * Callback method that will be invoked before an entity is persisted. Can return the same or a different instance of
     * the domain entity class.
     *
     * @param entity the entity being converted
     * @param index  must not be {@literal null}.
     * @return the entity to be converted
     */
    T onBeforeConvert(T entity, IndexCoordinates index);
}
