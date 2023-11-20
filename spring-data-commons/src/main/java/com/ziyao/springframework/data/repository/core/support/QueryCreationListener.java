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
package com.ziyao.springframework.data.repository.core.support;

import com.ziyao.springframework.data.repository.query.RepositoryQuery;

/**
 * Callback for listeners that want to execute functionality on {@link RepositoryQuery} creation.
 *
 * @author Oliver Gierke
 */
public interface QueryCreationListener<T extends RepositoryQuery> {

	/**
	 * Will be invoked just after the {@link RepositoryQuery} was created.
	 *
	 * @param query
	 */
	void onCreation(T query);
}
