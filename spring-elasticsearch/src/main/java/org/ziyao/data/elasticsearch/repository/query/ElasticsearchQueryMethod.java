/*
 * Copyright 2013-2023 the original author or authors.
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
package org.ziyao.data.elasticsearch.repository.query;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.ziyao.data.mapping.context.MappingContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.ziyao.data.annotation.AnnotatedElementUtils;
import org.ziyao.data.elasticsearch.annotations.CountQuery;
import org.ziyao.data.elasticsearch.annotations.Highlight;
import org.ziyao.data.elasticsearch.annotations.Query;
import org.ziyao.data.elasticsearch.core.SearchHit;
import org.ziyao.data.elasticsearch.core.SearchHits;
import org.ziyao.data.elasticsearch.core.SearchPage;
import org.ziyao.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.ziyao.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.ziyao.data.elasticsearch.core.query.HighlightQuery;
import org.ziyao.data.projection.ProjectionFactory;
import org.ziyao.data.repository.core.RepositoryMetadata;
import org.ziyao.data.repository.query.QueryMethod;
import org.ziyao.data.util.ClassTypeInformation;
import org.ziyao.data.util.Lazy;
import org.ziyao.data.util.TypeInformation;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * ElasticsearchQueryMethod
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Christoph Strobl
 * @author Peter-Josef Meisch
 */
public class ElasticsearchQueryMethod extends QueryMethod {

    private final MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext;
    private @Nullable ElasticsearchEntityMetadata<?> metadata;
    protected final Method method; // private in base class, but needed here and in derived classes as well
    @Nullable
    private final Query queryAnnotation;
    @Nullable
    private final Highlight highlightAnnotation;
    private final Lazy<HighlightQuery> highlightQueryLazy = Lazy.of(this::createAnnotatedHighlightQuery);

    public ElasticsearchQueryMethod(Method method, RepositoryMetadata repositoryMetadata, ProjectionFactory factory,
                                    MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext) {

        super(method, repositoryMetadata, factory);

        Assert.notNull(mappingContext, "MappingContext must not be null!");

        this.method = method;
        this.mappingContext = mappingContext;
        this.queryAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Query.class);
        this.highlightAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Highlight.class);

        verifyCountQueryTypes();
    }

    protected void verifyCountQueryTypes() {

        if (hasCountQueryAnnotation()) {
            TypeInformation<?> returnType = ClassTypeInformation.fromReturnTypeOf(method);

            if (returnType.getType() != long.class && !Long.class.isAssignableFrom(returnType.getType())) {
                throw new InvalidDataAccessApiUsageException("count query methods must return a Long");
            }
        }
    }

    public boolean hasAnnotatedQuery() {
        return this.queryAnnotation != null;
    }

    /**
     * @return the query String. Must not be {@literal null} when {@link #hasAnnotatedQuery()} returns true
     */
    public String getAnnotatedQuery() {
        return queryAnnotation.value();
    }

    /**
     * @return true if there is a {@link Highlight} annotation present.
     * @since 4.0
     */
    public boolean hasAnnotatedHighlight() {
        return highlightAnnotation != null;
    }

    /**
     * @return a {@link HighlightQuery} built from the {@link Highlight} annotation.
     * @throws IllegalArgumentException if no {@link Highlight} annotation is present on the method
     * @see #hasAnnotatedHighlight()
     */
    public HighlightQuery getAnnotatedHighlightQuery() {

        Assert.isTrue(hasAnnotatedHighlight(), "no Highlight annotation present on " + getName());

        return highlightQueryLazy.get();
    }

    private HighlightQuery createAnnotatedHighlightQuery() {
        return new HighlightQuery(
                org.ziyao.data.elasticsearch.core.query.highlight.Highlight.of(highlightAnnotation),
                getDomainClass());
    }

    /**
     * @return the {@link ElasticsearchEntityMetadata} for the query methods {@link #getReturnedObjectType() return type}.
     * @since 3.2
     */
    @Override
    public ElasticsearchEntityMetadata<?> getEntityInformation() {

        if (metadata == null) {

            Class<?> returnedObjectType = getReturnedObjectType();
            Class<?> domainClass = getDomainClass();

            if (ClassUtils.isPrimitiveOrWrapper(returnedObjectType)) {

                this.metadata = new SimpleElasticsearchEntityMetadata<>((Class<Object>) domainClass,
                        mappingContext.getRequiredPersistentEntity(domainClass));

            } else {

                ElasticsearchPersistentEntity<?> returnedEntity = mappingContext.getPersistentEntity(returnedObjectType);
                ElasticsearchPersistentEntity<?> managedEntity = mappingContext.getRequiredPersistentEntity(domainClass);
                returnedEntity = returnedEntity == null || returnedEntity.getType().isInterface() ? managedEntity
                        : returnedEntity;
                ElasticsearchPersistentEntity<?> collectionEntity = domainClass.isAssignableFrom(returnedObjectType)
                        ? returnedEntity
                        : managedEntity;

                this.metadata = new SimpleElasticsearchEntityMetadata<>((Class<Object>) returnedEntity.getType(),
                        collectionEntity);
            }
        }

        return this.metadata;
    }

    protected MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> getMappingContext() {
        return mappingContext;
    }

    /**
     * checks whether the return type of the underlying method is a
     * {@link SearchHits} or a collection of
     * {@link SearchHit}.
     *
     * @return true if the method has a {@link SearchHit}t related return type
     * @since 4.0
     */
    public boolean isSearchHitMethod() {
        Class<?> methodReturnType = method.getReturnType();

        if (SearchHits.class.isAssignableFrom(methodReturnType)) {
            return true;
        }

        try {
            // dealing with Collection<SearchHit<T>>, getting to T
            ParameterizedType methodGenericReturnType = ((ParameterizedType) method.getGenericReturnType());
            if (isAllowedGenericType(methodGenericReturnType)) {
                ParameterizedType collectionTypeArgument = (ParameterizedType) methodGenericReturnType
                        .getActualTypeArguments()[0];
                if (SearchHit.class.isAssignableFrom((Class<?>) collectionTypeArgument.getRawType())) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    /**
     * checks if the return type is {@link SearchPage}.
     *
     * @since 4.0
     */
    public boolean isSearchPageMethod() {
        return SearchPage.class.isAssignableFrom(methodReturnType());
    }

    /**
     * returns the declared return type for this method.
     *
     * @return the return type
     * @since 4.0
     */
    public Class<?> methodReturnType() {
        return method.getReturnType();
    }

    protected boolean isAllowedGenericType(ParameterizedType methodGenericReturnType) {
        return Collection.class.isAssignableFrom((Class<?>) methodGenericReturnType.getRawType())
                || Stream.class.isAssignableFrom((Class<?>) methodGenericReturnType.getRawType());
    }

    /**
     * checks whether the return type of the underlying method nether a
     * {@link SearchHits} nor a collection of
     * {@link SearchHit}.
     *
     * @return true if the method has not a {@link SearchHit}t related return
     * type
     * @since 4.0
     */
    public boolean isNotSearchHitMethod() {
        return !isSearchHitMethod();
    }

    /**
     * checks if the return type is not {@link SearchPage}.
     *
     * @since 4.2
     */
    public boolean isNotSearchPageMethod() {
        return !isSearchPageMethod();
    }

    /**
     * @return {@literal true} if the method is annotated with
     * {@link CountQuery} or with {@link Query}(count =true)
     * @since 4.2
     */
    public boolean hasCountQueryAnnotation() {
        return queryAnnotation != null && queryAnnotation.count();
    }

}
