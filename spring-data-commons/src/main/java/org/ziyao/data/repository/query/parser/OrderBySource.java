/*
 * Copyright 2008-2023 the original author or authors.
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
package org.ziyao.data.repository.query.parser;

import org.ziyao.data.mapping.PropertyPath;
import org.springframework.util.StringUtils;
import org.ziyao.data.domain.Sort;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple helper class to create a {@link Sort} instance from a method name end. It expects the last part of the method
 * name to be given and supports lining up multiple properties ending with the sorting direction. So the following
 * method ends are valid: {@code LastnameUsernameDesc}, {@code LastnameAscUsernameDesc}.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 * @author Mark Paluch
 */
class OrderBySource {

    static OrderBySource EMPTY = new OrderBySource("");

    private static final String BLOCK_SPLIT = "(?<=Asc|Desc)(?=\\p{Lu})";
    private static final Pattern DIRECTION_SPLIT = Pattern.compile("(.+?)(Asc|Desc)?$");
    private static final String INVALID_ORDER_SYNTAX = "Invalid order syntax for part %s";
    private static final Set<String> DIRECTION_KEYWORDS = new HashSet<>(Arrays.asList("Asc", "Desc"));

    private final List<Sort.Order> orders;

    /**
     * Creates a new {@link OrderBySource} for the given String clause not doing any checks whether the referenced
     * property actually exists.
     *
     * @param clause must not be {@literal null}.
     */
    OrderBySource(String clause) {
        this(clause, Optional.empty());
    }

    /**
     * Creates a new {@link OrderBySource} for the given clause, checking the property referenced exists on the given
     * type.
     *
     * @param clause      must not be {@literal null}.
     * @param domainClass must not be {@literal null}.
     */
    OrderBySource(String clause, Optional<Class<?>> domainClass) {

        this.orders = new ArrayList<>();

        if (!StringUtils.hasText(clause)) {
            return;
        }

        for (String part : clause.split(BLOCK_SPLIT)) {

            Matcher matcher = DIRECTION_SPLIT.matcher(part);

            if (!matcher.find()) {
                throw new IllegalArgumentException(String.format(INVALID_ORDER_SYNTAX, part));
            }

            String propertyString = matcher.group(1);
            String directionString = matcher.group(2);

            // No property, but only a direction keyword
            if (DIRECTION_KEYWORDS.contains(propertyString) && directionString == null) {
                throw new IllegalArgumentException(String.format(INVALID_ORDER_SYNTAX, part));
            }

            this.orders.add(createOrder(propertyString, Sort.Direction.fromOptionalString(directionString), domainClass));
        }
    }

    /**
     * Creates an {@link Sort.Order} instance from the given property source, direction and domain class. If the domain class
     * is given, we will use it for nested property traversal checks.
     *
     * @param propertySource
     * @param direction      must not be {@literal null}.
     * @param domainClass    must not be {@literal null}.
     * @return
     * @see PropertyPath#from(String, Class)
     */
    private Sort.Order createOrder(String propertySource, Optional<Sort.Direction> direction, Optional<Class<?>> domainClass) {

        return domainClass.map(type -> {

            PropertyPath propertyPath = PropertyPath.from(propertySource, type);
            return direction.map(it -> new Sort.Order(it, propertyPath.toDotPath()))
                    .orElseGet(() -> Sort.Order.by(propertyPath.toDotPath()));

        }).orElseGet(() -> direction//
                .map(it -> new Sort.Order(it, StringUtils.uncapitalize(propertySource)))
                .orElseGet(() -> Sort.Order.by(StringUtils.uncapitalize(propertySource))));
    }

    /**
     * Returns the clause as {@link Sort}.
     *
     * @return the {@link Sort}.
     */
    Sort toSort() {
        return Sort.by(this.orders);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Order By " + StringUtils.collectionToDelimitedString(orders, ", ");
    }
}
