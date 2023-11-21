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
package org.ziyao.data.elasticsearch.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ziyao.data.elasticsearch.core.query.Criteria;
import org.ziyao.data.elasticsearch.core.query.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;

/**
 * @param <T>
 * @param <ID>
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Sascha Woo
 * @author Murali Chevuri
 * @author Peter-Josef Meisch
 */
@NoRepositoryBean
public interface ElasticsearchRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

    /**
     * 分页搜索数据
     * <p>searchSimilar
     * entity中如果屬性{@code null}则不会被当做查询条件进行查询.
     * 如果全部为空则进行全量查询
     *
     * @param entity   被搜索实体类
     * @param pageable 分页信息
     * @return 返回搜到到的结果，分页展示
     */
    Page<T> searchSimilar(T entity, Pageable pageable);

    /**
     * 分页搜索数据
     * <p>
     * 会通过 fields中的字段进行搜索,如果为空则进行全量查询
     *
     * @param entity   被搜索实体类
     * @param fields   搜索条件
     * @param pageable 分页信息
     * @return 返回搜到到的结果，分页展示
     */
    Page<T> searchSimilar(T entity, @Nullable String[] fields, Pageable pageable);

    /**
     * 分页搜索数据
     *
     * @param entity   被搜索实体类
     * @param fields   搜索条件
     * @param pageable 分页信息
     * @param operator 关联类型。默认{@link Operator#And}
     * @return 返回搜到到的结果，分页展示
     */
    Page<T> searchSimilar(T entity, @Nullable String[] fields, Pageable pageable, Operator operator);

    /**
     * 分页搜索数据
     *
     * @param criteria 条件
     * @param pageable 分页信息
     * @return 返回搜到到的结果，分页展示
     */
    Page<T> searchSimilar(Criteria criteria, Pageable pageable);

    /**
     * 分页搜索数据
     *
     * @param query 搜索条件
     * @return 返回搜到到的结果，分页展示
     */
    Page<T> search(Query query);
}
