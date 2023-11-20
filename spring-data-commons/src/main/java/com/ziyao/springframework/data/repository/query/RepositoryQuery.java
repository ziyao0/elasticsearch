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
package com.ziyao.springframework.data.repository.query;

import org.springframework.lang.Nullable;

/**
 * Interface for a query abstraction.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 */
public interface RepositoryQuery {

	/**
	 * Executes the {@link RepositoryQuery} with the given parameters.
	 *
	 * @param parameters must not be {@literal null}.
	 * @return execution result. Can be {@literal null}.
	 */
	@Nullable
	Object execute(Object[] parameters);

	/**
	 * Returns the related {@link QueryMethod}.
	 *
	 * @return never {@literal null}.
	 */
	QueryMethod getQueryMethod();
}
