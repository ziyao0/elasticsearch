/*
 * Copyright 2011-2023 the original author or authors.
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
package org.ziyao.data.util;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * Base class for {@link TypeInformation} implementations that need parent type awareness.
 *
 * @author Oliver Gierke
 */
public abstract class ParentTypeAwareTypeInformation<S> extends TypeDiscoverer<S> {

    private final TypeDiscoverer<?> parent;
    private final Lazy<TypeDescriptor> descriptor;
    private int hashCode;

    /**
     * Creates a new {@link ParentTypeAwareTypeInformation}.
     *
     * @param type   must not be {@literal null}.
     * @param parent must not be {@literal null}.
     */
    protected ParentTypeAwareTypeInformation(Type type, TypeDiscoverer<?> parent) {
        this(type, parent, parent.getTypeVariableMap());
    }

    protected ParentTypeAwareTypeInformation(Type type, TypeDiscoverer<?> parent, Map<TypeVariable<?>, Type> map) {

        super(type, map);

        this.parent = parent;
        this.descriptor = Lazy.of(() -> new TypeDescriptor(toResolvableType(), null, null));
    }

    @Override
    public TypeDescriptor toTypeDescriptor() {
        return descriptor.get();
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.util.TypeDiscoverer#createInfo(java.lang.reflect.Type)
     */
    @Override
    protected TypeInformation<?> createInfo(Type fieldType) {

        if (parent.getType().equals(fieldType)) {
            return parent;
        }

        return super.createInfo(fieldType);
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.util.TypeDiscoverer#equals(java.lang.Object)
     */
    @Override
    protected ResolvableType toResolvableType() {
        return ResolvableType.forType(getType(), parent.toResolvableType());
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        ParentTypeAwareTypeInformation<?> that = (ParentTypeAwareTypeInformation<?>) obj;
        return this.parent == null ? that.parent == null : this.parent.equals(that.parent);
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.util.TypeDiscoverer#hashCode()
     */
    @Override
    public int hashCode() {

        if (this.hashCode == 0) {
            this.hashCode = super.hashCode() + (31 * parent.hashCode());
        }

        return this.hashCode;
    }
}
