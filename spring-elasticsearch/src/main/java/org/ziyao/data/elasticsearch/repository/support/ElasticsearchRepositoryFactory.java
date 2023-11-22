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
package org.ziyao.data.elasticsearch.repository.support;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.ziyao.data.elasticsearch.core.ElasticsearchOperations;
import org.ziyao.data.elasticsearch.repository.ElasticsearchRepository;
import org.ziyao.data.elasticsearch.repository.query.ElasticsearchPartQuery;
import org.ziyao.data.elasticsearch.repository.query.ElasticsearchQueryMethod;
import org.ziyao.data.elasticsearch.repository.query.ElasticsearchStringQuery;
import org.ziyao.data.projection.ProjectionFactory;
import org.ziyao.data.querydsl.QuerydslPredicateExecutor;
import org.ziyao.data.repository.core.NamedQueries;
import org.ziyao.data.repository.core.RepositoryInformation;
import org.ziyao.data.repository.core.RepositoryMetadata;
import org.ziyao.data.repository.core.support.RepositoryFactorySupport;
import org.ziyao.data.repository.query.QueryLookupStrategy;
import org.ziyao.data.repository.query.QueryLookupStrategy.Key;
import org.ziyao.data.repository.query.QueryMethodEvaluationContextProvider;
import org.ziyao.data.repository.query.RepositoryQuery;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.ziyao.data.querydsl.QuerydslUtils.QUERY_DSL_PRESENT;

/**
 * Factory to create {@link ElasticsearchRepository}
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Ryan Henszey
 * @author Gad Akuka
 * @author Mark Paluch
 * @author Christoph Strobl
 * @author Sascha Woo
 * @author Peter-Josef Meisch
 */
public class ElasticsearchRepositoryFactory extends RepositoryFactorySupport {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchEntityInformationCreator entityInformationCreator;

    public ElasticsearchRepositoryFactory(ElasticsearchOperations elasticsearchOperations) {

        Assert.notNull(elasticsearchOperations, "ElasticsearchOperations must not be null!");

        this.elasticsearchOperations = elasticsearchOperations;
        this.entityInformationCreator = new ElasticsearchEntityInformationCreatorImpl(
                elasticsearchOperations.getElasticsearchConverter().getMappingContext());
    }

    @Override
    public <T, ID> ElasticsearchEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return entityInformationCreator.getEntityInformation(domainClass);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
        return getTargetRepositoryViaReflection(metadata, getEntityInformation(metadata.getDomainType()),
                elasticsearchOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isQueryDslRepository(metadata.getRepositoryInterface())) {
            throw new IllegalArgumentException("QueryDsl Support has not been implemented yet.");
        }

        return SimpleElasticsearchRepository.class;
    }

    private static boolean isQueryDslRepository(Class<?> repositoryInterface) {
        return QUERY_DSL_PRESENT && QuerydslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable Key key,
                                                                   QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(new ElasticsearchQueryLookupStrategy());
    }

    private class ElasticsearchQueryLookupStrategy implements QueryLookupStrategy {

        /*
         * (non-Javadoc)
         * @see org.ziyao.data.repository.query.QueryLookupStrategy#resolveQuery(java.lang.reflect.Method, org.ziyao.data.repository.core.RepositoryMetadata, org.ziyao.data.projection.ProjectionFactory, org.ziyao.data.repository.core.NamedQueries)
         */
        @Override
        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
                                            NamedQueries namedQueries) {

            ElasticsearchQueryMethod queryMethod = new ElasticsearchQueryMethod(method, metadata, factory,
                    elasticsearchOperations.getElasticsearchConverter().getMappingContext());
            String namedQueryName = queryMethod.getNamedQueryName();

            if (namedQueries.hasQuery(namedQueryName)) {
                String namedQuery = namedQueries.getQuery(namedQueryName);
                return new ElasticsearchStringQuery(queryMethod, elasticsearchOperations, namedQuery);
            } else if (queryMethod.hasAnnotatedQuery()) {
                return new ElasticsearchStringQuery(queryMethod, elasticsearchOperations, queryMethod.getAnnotatedQuery());
            }
            return new ElasticsearchPartQuery(queryMethod, elasticsearchOperations);
        }
    }

    @Override
    protected RepositoryMetadata getRepositoryMetadata(Class<?> repositoryInterface) {
        return new ElasticsearchRepositoryMetadata(repositoryInterface);
    }
}
