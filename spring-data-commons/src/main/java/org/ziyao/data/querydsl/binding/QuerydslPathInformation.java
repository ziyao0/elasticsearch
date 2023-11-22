/*
 * Copyright 2016-2023 the original author or authors.
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
package org.ziyao.data.querydsl.binding;

import com.querydsl.core.types.Path;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.ziyao.data.querydsl.EntityPathResolver;
import org.ziyao.data.querydsl.QuerydslUtils;

import java.beans.PropertyDescriptor;

/**
 * {@link PathInformation} based on a Querydsl {@link Path}.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Johannes Englmeier
 * @since 1.13
 */
class QuerydslPathInformation implements PathInformation {

    private final Path<?> path;

    private QuerydslPathInformation(Path<?> path) {
        this.path = path;
    }

    public static QuerydslPathInformation of(Path<?> path) {
        return new QuerydslPathInformation(path);
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.querydsl.binding.PathInformation#getRootParentType()
     */
    @Override
    public Class<?> getRootParentType() {
        return path.getRoot().getType();
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.querydsl.binding.PathInformation#getLeafType()
     */
    @Override
    public Class<?> getLeafType() {
        return path.getType();
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.querydsl.binding.PathInformation#getLeafParentType()
     */
    @Override
    public Class<?> getLeafParentType() {

        Path<?> parent = path.getMetadata().getParent();

        if (parent == null) {
            throw new IllegalStateException(String.format("Could not obtain metadata for parent node of %s", path));
        }

        return parent.getType();
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.querydsl.binding.PathInformation#getLeafProperty()
     */
    @Override
    public String getLeafProperty() {
        return path.getMetadata().getElement().toString();
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.querydsl.binding.PathInformation#getLeafPropertyDescriptor()
     */
    @Nullable
    @Override
    public PropertyDescriptor getLeafPropertyDescriptor() {
        return BeanUtils.getPropertyDescriptor(getLeafParentType(), getLeafProperty());
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.querydsl.binding.PathInformation#toDotPath()
     */
    @Override
    public String toDotPath() {
        return QuerydslUtils.toDotPath(path);
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.querydsl.binding.PathInformation#reifyPath(org.ziyao.data.querydsl.EntityPathResolver)
     */
    public Path<?> reifyPath(EntityPathResolver resolver) {
        return path;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof PathInformation)) {
            return false;
        }

        PathInformation that = (PathInformation) o;
        return ObjectUtils.nullSafeEquals(getRootParentType(), that.getRootParentType())
                && ObjectUtils.nullSafeEquals(toDotPath(), that.toDotPath());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(getRootParentType());
        result = 31 * result + ObjectUtils.nullSafeHashCode(toDotPath());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QuerydslPathInformation(path=" + this.path + ")";
    }
}
