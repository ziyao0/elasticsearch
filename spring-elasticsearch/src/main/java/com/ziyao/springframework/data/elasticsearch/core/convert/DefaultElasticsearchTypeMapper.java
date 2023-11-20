/*
 * Copyright 2019-2023 the original author or authors.
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
package com.ziyao.springframework.data.elasticsearch.core.convert;

import com.ziyao.springframework.data.convert.DefaultTypeMapper;
import com.ziyao.springframework.data.convert.SimpleTypeInformationMapper;
import com.ziyao.springframework.data.convert.TypeAliasAccessor;
import com.ziyao.springframework.data.convert.TypeInformationMapper;
import com.ziyao.springframework.data.mapping.Alias;
import com.ziyao.springframework.data.mapping.PersistentEntity;
import com.ziyao.springframework.data.mapping.context.MappingContext;
import com.ziyao.springframework.data.util.ClassTypeInformation;
import com.ziyao.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch specific {@link com.ziyao.springframework.data.convert.TypeMapper} implementation.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 3.2
 */
public class DefaultElasticsearchTypeMapper extends DefaultTypeMapper<Map<String, Object>>
		implements ElasticsearchTypeMapper {

	@SuppressWarnings("rawtypes") //
	private static final TypeInformation<Map> MAP_TYPE_INFO = ClassTypeInformation.from(Map.class);

	private final @Nullable String typeKey;

	public DefaultElasticsearchTypeMapper(@Nullable String typeKey) {
		this(typeKey, Collections.singletonList(new SimpleTypeInformationMapper()));
	}

	public DefaultElasticsearchTypeMapper(@Nullable String typeKey,
			MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext) {
		this(typeKey, new MapTypeAliasAccessor(typeKey), mappingContext,
				Collections.singletonList(new SimpleTypeInformationMapper()));
	}

	public DefaultElasticsearchTypeMapper(@Nullable String typeKey, List<? extends TypeInformationMapper> mappers) {
		this(typeKey, new MapTypeAliasAccessor(typeKey), null, mappers);
	}

	public DefaultElasticsearchTypeMapper(@Nullable String typeKey, TypeAliasAccessor<Map<String, Object>> accessor,
			@Nullable MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext,
			List<? extends TypeInformationMapper> mappers) {

		super(accessor, mappingContext, mappers);
		this.typeKey = typeKey;
	}

	@Override
	public boolean isTypeKey(String key) {
		return typeKey != null && typeKey.equals(key);
	}

	@Override
	@Nullable
	public String getTypeKey() {
		return typeKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ziyao.springframework.data.convert.DefaultTypeMapper#getFallbackTypeFor(java.lang.Object)
	 */
	@Override
	protected TypeInformation<?> getFallbackTypeFor(Map<String, Object> source) {
		return MAP_TYPE_INFO;
	}

	/**
	 * {@link TypeAliasAccessor} to store aliases in a {@link Map}.
	 *
	 * @author Christoph Strobl
	 */
	public static class MapTypeAliasAccessor implements TypeAliasAccessor<Map<String, Object>> {

		private final @Nullable String typeKey;

		public MapTypeAliasAccessor(@Nullable String typeKey) {
			this.typeKey = typeKey;
		}

		/*
		 * (non-Javadoc)
		 * @see com.ziyao.springframework.data.convert.TypeAliasAccessor#readAliasFrom(java.lang.Object)
		 */
		@Override
		public Alias readAliasFrom(Map<String, Object> source) {
			return Alias.ofNullable(source.get(typeKey));
		}

		/*
		 * (non-Javadoc)
		 * @see com.ziyao.springframework.data.convert.TypeAliasAccessor#writeTypeTo(java.lang.Object, java.lang.Object)
		 */
		@Override
		public void writeTypeTo(Map<String, Object> sink, Object alias) {

			if (typeKey == null) {
				return;
			}

			sink.put(typeKey, alias);
		}
	}
}