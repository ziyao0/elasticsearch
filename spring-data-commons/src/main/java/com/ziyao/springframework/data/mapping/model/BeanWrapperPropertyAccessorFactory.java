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
package com.ziyao.springframework.data.mapping.model;

import com.ziyao.springframework.data.mapping.PersistentEntity;
import com.ziyao.springframework.data.mapping.PersistentPropertyAccessor;

/**
 * PersistentPropertyAccessorFactory that uses a {@link BeanWrapper}.
 *
 * @author Oliver Gierke
 */
public enum BeanWrapperPropertyAccessorFactory implements PersistentPropertyAccessorFactory {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.ziyao.springframework.data.mapping.model.PersistentPropertyAccessorFactory#getPropertyAccessor(com.ziyao.springframework.data.mapping.PersistentEntity, java.lang.Object)
	 */
	@Override
	public <T> PersistentPropertyAccessor<T> getPropertyAccessor(PersistentEntity<?, ?> entity, T bean) {
		return new BeanWrapper<>(bean);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ziyao.springframework.data.mapping.model.PersistentPropertyAccessorFactory#isSupported(com.ziyao.springframework.data.mapping.PersistentEntity)
	 */
	@Override
	public boolean isSupported(PersistentEntity<?, ?> entity) {
		return true;
	}
}