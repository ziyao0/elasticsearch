//package org.ziyao.data.elasticsearch.repository.support;
//
//import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
//import org.ziyao.data.domain.Page;
//import org.ziyao.data.domain.Pageable;
//import org.ziyao.data.elasticsearch.core.ElasticsearchOperations;
//import org.ziyao.data.elasticsearch.core.SearchHitSupport;
//import org.ziyao.data.elasticsearch.core.SearchHits;
//import org.ziyao.data.elasticsearch.core.SearchPage;
//import org.ziyao.data.elasticsearch.core.query.Criteria;
//import org.ziyao.data.elasticsearch.core.query.CriteriaQuery;
//import org.ziyao.data.elasticsearch.core.query.Query;
//import org.ziyao.data.elasticsearch.repository.ElasticsearchRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.lang.NonNull;
//import org.springframework.lang.Nullable;
//import org.springframework.util.Assert;
//import org.springframework.util.ObjectUtils;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author ziyao zhang
// * @since 2023/11/20
// */
//public class BoostElasticsearchRepository<T, ID> extends SimpleElasticsearchRepository<T, ID> implements ElasticsearchRepository <T, ID>{
//    private static final Logger LOGGER = LoggerFactory.getLogger(BoostElasticsearchRepository.class);
//    private final BeanPropertyExtractor<T> beanPropertyExtractor;
//
//    public BoostElasticsearchRepository(ElasticsearchEntityInformation<T, ID> metadata,
//                                        ElasticsearchOperations operations) {
//        super(metadata, operations);
//        beanPropertyExtractor = new BeanPropertyExtractor<>(metadata.getJavaType());
//    }
//
//    @Override
//    public Page<T> searchSimilar(T entity, Pageable pageable) {
//        return searchSimilar(entity, null, pageable);
//    }
//
//    @Override
//    public Page<T> searchSimilar(@NonNull T entity, @Nullable String[] fields, @NonNull Pageable pageable) {
//        return searchSimilar(entity, fields, pageable, Operator.And);
//    }
//
//
//    @Override
//    public Page<T> searchSimilar(T entity, @Nullable String[] fields, Pageable pageable, Operator operator) {
//        Assert.notNull(entity, "Cannot search similar records for 'null'.");
//        Assert.notNull(pageable, "'pageable' cannot be 'null'");
//
//        return search(
//                CriteriaQuery.builder(
//                                createCriteria(entity, fields, operator))
//                        .withPageable(pageable).build());
//    }
//
//    @Override
//    public Page<T> searchSimilar(Criteria criteria, Pageable pageable) {
//        Assert.notNull(criteria, "Query 不能为空");
//        Assert.notNull(pageable, "Query 不能为空");
//        return search(
//                CriteriaQuery.builder(criteria).withPageable(pageable).build());
//    }
//
//    @Override
//    public Page<T> search(Query query) {
//        Assert.notNull(query, "Query 不能为空");
//        return doSearch(query);
//    }
//
//    /**
//     * 查询核心方法
//     */
//    @SuppressWarnings("unchecked")
//    private Page<T> doSearch(Query query) {
//
//        SearchHits<T> searchHits = operations.search(query, entityClass);
//        SearchPage<T> searchPage = SearchHitSupport.searchPageFor(searchHits, query.getPageable());
//        return (Page<T>) SearchHitSupport.unwrapSearchHits(searchPage);
//    }
//
//    /**
//     * 创建并组装查询条件
//     */
//    private Criteria createCriteria(T entity, @Nullable String[] fields, Operator operator) {
//        Criteria criteria = new Criteria();
//
//        Map<String, Object> properties = extractPropertyFromEntity(entity, fields);
//        doCreateCriteria(properties).forEach(condition -> {
//            switch (operator) {
//                case And:
//                    criteria.and(condition);
//                    break;
//                case Or:
//                    criteria.or(condition);
//                    break;
//                default:
//                    LOGGER.error("未知的操作类型:{}", operator.jsonValue());
//            }
//        });
//        return criteria;
//    }
//
//    /**
//     * 创建查询条件
//     */
//    private List<Criteria> doCreateCriteria(Map<String, Object> properties) {
//        List<Criteria> criteriaList = new ArrayList<>();
//        for (Map.Entry<String, Object> property : properties.entrySet()) {
//
//            Object value = property.getValue();
//            //  skip value is null
//            if (!ObjectUtils.isEmpty(value)) {
//                criteriaList.add(new Criteria(property.getKey()).is(property.getValue()));
//            }
//        }
//        return criteriaList;
//    }
//
//    /**
//     * 从给定的实体类中提取请求参数和值，返回key-value格式
//     *
//     * @param entity 实体类
//     * @param fields 给定字段数组，如果不为空则从实体类中提取该数组中字段的值
//     * @return 返回key-value格式的集合
//     */
//    private Map<String, Object> extractPropertyFromEntity(T entity, @Nullable String[] fields) {
//        if (fields == null) {
//            return beanPropertyExtractor.extract(entity);
//        }
//        Map<String, Object> properties = new HashMap<>();
//        for (String field : fields) {
//            properties.put(field, beanPropertyExtractor.extract(entity, field));
//        }
//        return properties;
//    }
//}
