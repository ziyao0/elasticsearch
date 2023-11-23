/*
 * Copyright 2014-2023 the original author or authors.
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
package org.ziyao.data.repository.core.support;

import org.ziyao.data.mapping.IdentifierAccessor;
import org.ziyao.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.lang.Nullable;
import org.ziyao.data.repository.core.EntityInformation;

/**
 * {@link EntityInformation} implementation that uses a {@link PersistentEntity} to obtain id type information and uses
 * a {@link IdentifierAccessor} to access the property value if requested.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 */
public class PersistentEntityInformation<T, ID> implements EntityInformation<T, ID> {

    private final PersistentEntity<T, ? extends PersistentProperty<?>> persistentEntity;

    public PersistentEntityInformation(PersistentEntity<T, ? extends PersistentProperty<?>> persistentEntity) {
        this.persistentEntity = persistentEntity;
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.repository.core.support.AbstractEntityInformation#isNew(java.lang.Object)
     */
    @Override
    public boolean isNew(T entity) {
        return persistentEntity.isNew(entity);
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.repository.core.EntityInformation#getId(java.lang.Object)
     */
    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public ID getId(T entity) {
        return (ID) persistentEntity.getIdentifierAccessor(entity).getIdentifier();
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.repository.core.EntityMetadata#getJavaType()
     */
    @Override
    public Class<T> getJavaType() {
        return persistentEntity.getType();
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.repository.core.EntityInformation#getIdType()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<ID> getIdType() {
        return (Class<ID>) persistentEntity.getRequiredIdProperty().getType();
    }
}
