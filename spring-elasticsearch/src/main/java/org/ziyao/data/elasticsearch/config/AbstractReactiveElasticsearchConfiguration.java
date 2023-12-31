/*
 * Copyright 2018-2023 the original author or authors.
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
package org.ziyao.data.elasticsearch.config;

import org.elasticsearch.action.support.IndicesOptions;
import org.springframework.lang.Nullable;
import org.ziyao.data.elasticsearch.core.RefreshPolicy;

/**
 * @author Christoph Strobl
 * @author Peter-Josef Meisch
 * @see ElasticsearchConfigurationSupport
 * @since 3.2
 */
public abstract class AbstractReactiveElasticsearchConfiguration extends ElasticsearchConfigurationSupport {


    /**
     * Set up the write {@link RefreshPolicy}. Default is set to null to use the cluster defaults..
     *
     * @return {@literal null} to use the server defaults.
     */
    @Nullable
    protected RefreshPolicy refreshPolicy() {
        return null;
    }

    /**
     * Set up the read {@link IndicesOptions}. Default is set to {@link IndicesOptions#strictExpandOpenAndForbidClosed()}.
     *
     * @return {@literal null} to use the server defaults.
     */
    @Nullable
    protected IndicesOptions indicesOptions() {
        return IndicesOptions.strictExpandOpenAndForbidClosed();
    }

}
