/*
 * Copyright 2015-2023 the original author or authors.
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

import org.ziyao.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.ziyao.data.mapping.PreferredConstructor;
import org.ziyao.data.mapping.model.EntityInstantiators;
import org.ziyao.data.mapping.model.InternalEntityInstantiatorFactory;
import org.ziyao.data.mapping.model.ParameterValueProvider;

/**
 * An {@link EntityInstantiator} that can generate byte code to speed-up dynamic object instantiation. Uses the
 * {@link PersistentEntity}'s {@link PreferredConstructor} to instantiate an instance of the entity by dynamically
 * generating factory methods with appropriate constructor invocations via ASM. If we cannot generate byte code for a
 * type, we gracefully fall-back to the {@link ReflectionEntityInstantiator}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @author Phillip Webb
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.11
 * @deprecated since 2.3, use {@code org.ziyao.data.mapping.model.ClassGeneratingEntityInstantiator} through
 * {@link EntityInstantiators}.
 */
@Deprecated
public class ClassGeneratingEntityInstantiator implements EntityInstantiator {

    private final org.ziyao.data.mapping.model.EntityInstantiator delegate = InternalEntityInstantiatorFactory
            .getClassGeneratingEntityInstantiator();

    /**
     * Creates a new {@link ClassGeneratingEntityInstantiator}.
     */
    public ClassGeneratingEntityInstantiator() {
        super();
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
