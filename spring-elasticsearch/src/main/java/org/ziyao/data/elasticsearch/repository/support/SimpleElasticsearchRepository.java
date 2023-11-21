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

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.ziyao.data.elasticsearch.core.*;
import org.ziyao.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.ziyao.data.elasticsearch.core.mapping.IndexCoordinates;
import org.ziyao.data.elasticsearch.core.query.*;
import org.ziyao.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Elasticsearch specific repository implementation. Likely to be used as target within
 * {@link ElasticsearchRepositoryFactory}
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Ryan Henszey
 * @author Kevin Leturc
 * @author Mark Paluch
 * @author Christoph Strobl
 * @author Michael Wirth
 * @author Sascha Woo
 * @author Murali Chevuri
 * @author Peter-Josef Meisch
 * @author Aleksei Arsenev
 * @author Jens Schauder
 */
public  class SimpleElasticsearchRepository<T, ID> implements ElasticsearchRepository<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleElasticsearchRepository.class);

    protected ElasticsearchOperations operations;
    protected IndexOperations indexOperations;

    protected Class<T> entityClass;
    protected ElasticsearchEntityInformation<T, ID> entityInformation;
    private final BeanPropertyExtractor<T> beanPropertyExtractor;
    public SimpleElasticsearchRepository(ElasticsearchEntityInformation<T, ID> metadata,
                                         ElasticsearchOperations operations) {
        this.operations = operations;

        Assert.notNull(metadata, "ElasticsearchEntityInformation must not be null!");

        this.entityInformation = metadata;
        this.entityClass = this.entityInformation.getJavaType();
        this.indexOperations = operations.indexOps(this.entityClass);
        beanPropertyExtractor = new BeanPropertyExtractor<>(metadata.getJavaType());

        if (shouldCreateIndexAndMapping() && !indexOperations.exists()) {
            indexOperations.createWithMapping();
        }
    }

    private boolean shouldCreateIndexAndMapping() {

        final ElasticsearchPersistentEntity<?> entity = operations.getElasticsearchConverter().getMappingContext()
                .getRequiredPersistentEntity(entityClass);
        return entity.isCreateIndexAndMapping();
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(
                execute(operations -> operations.get(stringIdRepresentation(id), entityClass, getIndexCoordinates())));
    }

    @Override
    public Iterable<T> findAll() {
        int itemCount = (int) this.count();

        if (itemCount == 0) {
            return new PageImpl<>(Collections.emptyList());
        }
        return this.findAll(PageRequest.of(0, Math.max(1, itemCount)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<T> findAll(Pageable pageable) {

        Assert.notNull(pageable, "pageable must not be null");

        Query query = Query.findAll();
        query.setPageable(pageable);
        SearchHits<T> searchHits = execute(operations -> operations.search(query, entityClass, getIndexCoordinates()));
        SearchPage<T> page = SearchHitSupport.searchPageFor(searchHits, query.getPageable());
        // noinspection ConstantConditions
        return (Page<T>) SearchHitSupport.unwrapSearchHits(page);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<T> findAll(Sort sort) {

        Assert.notNull(sort, "sort must not be null");

        int itemCount = (int) this.count();

        if (itemCount == 0) {
            return new PageImpl<>(Collections.emptyList());
        }
        Pageable pageable = PageRequest.of(0, itemCount, sort);
        Query query = Query.findAll();
        query.setPageable(pageable);
        List<SearchHit<T>> searchHitList = execute(
                operations -> operations.search(query, entityClass, getIndexCoordinates()).getSearchHits());
        // noinspection ConstantConditions
        return (List<T>) SearchHitSupport.unwrapSearchHits(searchHitList);
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {

        Assert.notNull(ids, "ids can't be null.");

        List<T> result = new ArrayList<>();
        Query idQuery = getIdQuery(ids);
        if (StringUtils.isEmpty(idQuery.getIds())) {
            return result;
        }

        List<MultiGetItem<T>> multiGetItems = execute(
                operations -> operations.multiGet(idQuery, entityClass, getIndexCoordinates()));

        if (multiGetItems != null) {
            multiGetItems.forEach(multiGetItem -> {

                if (multiGetItem.hasItem()) {
                    result.add(multiGetItem.getItem());
                }
            });
        }

        return result;
    }

    @Override
    public long count() {
        Query query = Query.findAll();
        ((BaseQuery) query).setMaxResults(0);
        return execute(operations -> operations.count(query, entityClass, getIndexCoordinates()));
    }

    @Override
    public <S extends T> S save(S entity) {

        Assert.notNull(entity, "Cannot save 'null' entity.");

        // noinspection ConstantConditions
        return executeAndRefresh(operations -> operations.save(entity, getIndexCoordinates()));
    }

    public <S extends T> List<S> save(List<S> entities) {

        Assert.notNull(entities, "Cannot insert 'null' as a List.");

        return Streamable.of(saveAll(entities)).stream().collect(Collectors.toList());
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {

        Assert.notNull(entities, "Cannot insert 'null' as a List.");

        IndexCoordinates indexCoordinates = getIndexCoordinates();
        executeAndRefresh(operations -> operations.save(entities, indexCoordinates));

        return entities;
    }

    @Override
    public boolean existsById(ID id) {
        // noinspection ConstantConditions
        return execute(operations -> operations.exists(stringIdRepresentation(id), getIndexCoordinates()));
    }

    @Override
    public void deleteById(ID id) {

        Assert.notNull(id, "Cannot delete entity with id 'null'.");

        doDelete(id, null, getIndexCoordinates());
    }

    @Override
    public void delete(T entity) {

        Assert.notNull(entity, "Cannot delete 'null' entity.");

        doDelete(extractIdFromBean(entity), operations.getEntityRouting(entity), getIndexCoordinates());
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {

        Assert.notNull(ids, "Cannot delete 'null' list.");

        List<String> idStrings = new ArrayList<>();
        for (ID id : ids) {
            idStrings.add(stringIdRepresentation(id));
        }

        if (idStrings.isEmpty()) {
            return;
        }

        Query query = operations.idsQuery(idStrings);
        executeAndRefresh((OperationsCallback<Void>) operations -> {
            operations.delete(query, entityClass, getIndexCoordinates());
            return null;
        });
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {

        Assert.notNull(entities, "Cannot delete 'null' list.");

        List<ID> ids = new ArrayList<>();
        for (T entity : entities) {
            ID id = extractIdFromBean(entity);
            if (id != null) {
                ids.add(id);
            }
        }

        deleteAllById(ids);
    }

    private void doDelete(@Nullable ID id, @Nullable String routing, IndexCoordinates indexCoordinates) {

        if (id != null) {
            executeAndRefresh(operations -> operations.delete(stringIdRepresentation(id), routing, indexCoordinates));
        }
    }

    @Override
    public void deleteAll() {
        IndexCoordinates indexCoordinates = getIndexCoordinates();

        executeAndRefresh((OperationsCallback<Void>) operations -> {
            operations.delete(Query.findAll(), entityClass, indexCoordinates);
            return null;
        });
    }

    private void doRefresh() {
        RefreshPolicy refreshPolicy = null;

        if (operations instanceof AbstractElasticsearchTemplate) {
            refreshPolicy = ((AbstractElasticsearchTemplate) operations).getRefreshPolicy();
        }

        if (refreshPolicy == null) {
            indexOperations.refresh();
        }
    }

    // region helper functions
    @Nullable
    protected ID extractIdFromBean(T entity) {
        return entityInformation.getId(entity);
    }

    private List<String> stringIdsRepresentation(Iterable<? extends ID> ids) {

        Assert.notNull(ids, "ids can't be null.");

        return StreamUtils.createStreamFromIterator(ids.iterator()).map(id -> stringIdRepresentation(id))
                .collect(Collectors.toList());
    }

    protected @Nullable String stringIdRepresentation(@Nullable ID id) {
        return operations.stringIdRepresentation(id);
    }

    private IndexCoordinates getIndexCoordinates() {
        return operations.getIndexCoordinatesFor(entityClass);
    }

    private Query getIdQuery(Iterable<? extends ID> ids) {
        List<String> stringIds = stringIdsRepresentation(ids);

        return new NativeSearchQueryBuilder().withIds(stringIds).build();
    }
    // endregion

    // region operations callback
    @FunctionalInterface
    public interface OperationsCallback<R> {
        @Nullable
        R doWithOperations(ElasticsearchOperations operations);
    }

    @Nullable
    public <R> R execute(OperationsCallback<R> callback) {
        return callback.doWithOperations(operations);
    }

    @Nullable
    public <R> R executeAndRefresh(OperationsCallback<R> callback) {
        R result = callback.doWithOperations(operations);
        doRefresh();
        return result;
    }
    // endregion

    @Override
    public Page<T> searchSimilar(T entity, Pageable pageable) {
        return searchSimilar(entity, null, pageable);
    }

    @Override
    public Page<T> searchSimilar(@NonNull T entity, @Nullable String[] fields, @NonNull Pageable pageable) {
        return searchSimilar(entity, fields, pageable, Operator.And);
    }


    @Override
    public Page<T> searchSimilar(T entity, @Nullable String[] fields, Pageable pageable, Operator operator) {
        Assert.notNull(entity, "Cannot search similar records for 'null'.");
        Assert.notNull(pageable, "'pageable' cannot be 'null'");

        return search(
                CriteriaQuery.builder(
                                createCriteria(entity, fields, operator))
                        .withPageable(pageable).build());
    }

    @Override
    public Page<T> searchSimilar(Criteria criteria, Pageable pageable) {
        Assert.notNull(criteria, "Query 不能为空");
        Assert.notNull(pageable, "Query 不能为空");
        return search(
                CriteriaQuery.builder(criteria).withPageable(pageable).build());
    }

    @Override
    public Page<T> search(Query query) {
        Assert.notNull(query, "Query 不能为空");
        return doSearch(query);
    }

    /**
     * 查询核心方法
     */
    @SuppressWarnings("unchecked")
    private Page<T> doSearch(Query query) {

        SearchHits<T> searchHits = operations.search(query, entityClass);
        SearchPage<T> searchPage = SearchHitSupport.searchPageFor(searchHits, query.getPageable());
        return (Page<T>) SearchHitSupport.unwrapSearchHits(searchPage);
    }

    /**
     * 创建并组装查询条件
     */
    private Criteria createCriteria(T entity, @Nullable String[] fields, Operator operator) {
        Criteria criteria = new Criteria();

        Map<String, Object> properties = extractPropertyFromEntity(entity, fields);
        doCreateCriteria(properties).forEach(condition -> {
            switch (operator) {
                case And:
                    criteria.and(condition);
                    break;
                case Or:
                    criteria.or(condition);
                    break;
                default:
                    LOGGER.error("未知的操作类型:{}", operator.jsonValue());
            }
        });
        return criteria;
    }

    /**
     * 创建查询条件
     */
    private List<Criteria> doCreateCriteria(Map<String, Object> properties) {
        List<Criteria> criteriaList = new ArrayList<>();
        for (Map.Entry<String, Object> property : properties.entrySet()) {

            Object value = property.getValue();
            //  skip value is null
            if (!ObjectUtils.isEmpty(value)) {
                criteriaList.add(new Criteria(property.getKey()).is(property.getValue()));
            }
        }
        return criteriaList;
    }

    /**
     * 从给定的实体类中提取请求参数和值，返回key-value格式
     *
     * @param entity 实体类
     * @param fields 给定字段数组，如果不为空则从实体类中提取该数组中字段的值
     * @return 返回key-value格式的集合
     */
    private Map<String, Object> extractPropertyFromEntity(T entity, @Nullable String[] fields) {
        if (fields == null) {
            return beanPropertyExtractor.extract(entity);
        }
        Map<String, Object> properties = new HashMap<>();
        for (String field : fields) {
            properties.put(field, beanPropertyExtractor.extract(entity, field));
        }
        return properties;
    }
}
