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
package org.ziyao.data.convert;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ParameterValueProvider;

/**
 * Adapter to bridge calls from the new APIs back to the legacy ones.
 *
 * @author Oliver Drotbohm
 */
class EntityInstantiatorAdapter implements EntityInstantiator {

    private final org.springframework.data.mapping.model.EntityInstantiator delegate;

    EntityInstantiatorAdapter(org.springframework.data.mapping.model.EntityInstantiator delegate) {
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.mapping.model.EntityInstantiator#createInstance(org.ziyao.data.mapping.PersistentEntity, org.ziyao.data.mapping.model.ParameterValueProvider)
     */
    @Override
    public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity,
                                                                                                             ParameterValueProvider<P> provider) {
        return delegate.createInstance(entity, provider);
    }
}
