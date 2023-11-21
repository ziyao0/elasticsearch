/*
 * Copyright 2021-2023 the original author or authors.
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
package org.ziyao.data.elasticsearch.core.query;

import java.util.EnumSet;

/**
 * Class mirroring the IndicesOptions from Elasticsearch in Spring Data Elasticsearch API.
 *
 * @author Peter-Josef Meisch
 * @since 4.3
 */
public class IndicesOptions {

	private EnumSet<Option> options;
	private EnumSet<WildcardStates> expandWildcards;

	public static final IndicesOptions STRICT_EXPAND_OPEN = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES), EnumSet.of(WildcardStates.OPEN));
	public static final IndicesOptions STRICT_EXPAND_OPEN_HIDDEN = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES),
			EnumSet.of(WildcardStates.OPEN, WildcardStates.HIDDEN));
	public static final IndicesOptions LENIENT_EXPAND_OPEN = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES, Option.IGNORE_UNAVAILABLE),
			EnumSet.of(WildcardStates.OPEN));
	public static final IndicesOptions LENIENT_EXPAND_OPEN_HIDDEN = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES, Option.IGNORE_UNAVAILABLE),
			EnumSet.of(WildcardStates.OPEN, WildcardStates.HIDDEN));
	public static final IndicesOptions LENIENT_EXPAND_OPEN_CLOSED = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES, Option.IGNORE_UNAVAILABLE),
			EnumSet.of(WildcardStates.OPEN, WildcardStates.CLOSED));
	public static final IndicesOptions LENIENT_EXPAND_OPEN_CLOSED_HIDDEN = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES, Option.IGNORE_UNAVAILABLE),
			EnumSet.of(WildcardStates.OPEN, WildcardStates.CLOSED,
					WildcardStates.HIDDEN));
	public static final IndicesOptions STRICT_EXPAND_OPEN_CLOSED = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES),
			EnumSet.of(WildcardStates.OPEN, WildcardStates.CLOSED));
	public static final IndicesOptions STRICT_EXPAND_OPEN_CLOSED_HIDDEN = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES), EnumSet.of(WildcardStates.OPEN,
					WildcardStates.CLOSED, WildcardStates.HIDDEN));
	public static final IndicesOptions STRICT_EXPAND_OPEN_FORBID_CLOSED = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES, Option.FORBID_CLOSED_INDICES),
			EnumSet.of(WildcardStates.OPEN));
	public static final IndicesOptions STRICT_EXPAND_OPEN_HIDDEN_FORBID_CLOSED = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES, Option.FORBID_CLOSED_INDICES),
			EnumSet.of(WildcardStates.OPEN, WildcardStates.HIDDEN));
	public static final IndicesOptions STRICT_EXPAND_OPEN_FORBID_CLOSED_IGNORE_THROTTLED = new IndicesOptions(
			EnumSet.of(Option.ALLOW_NO_INDICES, Option.FORBID_CLOSED_INDICES,
					Option.IGNORE_THROTTLED),
			EnumSet.of(WildcardStates.OPEN));
	public static final IndicesOptions STRICT_SINGLE_INDEX_NO_EXPAND_FORBID_CLOSED = new IndicesOptions(
			EnumSet.of(Option.FORBID_ALIASES_TO_MULTIPLE_INDICES, Option.FORBID_CLOSED_INDICES),
			EnumSet.noneOf(WildcardStates.class));

	public IndicesOptions(EnumSet<Option> options, EnumSet<WildcardStates> expandWildcards) {
		this.options = options;
		this.expandWildcards = expandWildcards;
	}

	public EnumSet<Option> getOptions() {
		return options;
	}

	public EnumSet<WildcardStates> getExpandWildcards() {
		return expandWildcards;
	}

	public enum WildcardStates {
		OPEN, CLOSED, HIDDEN;
	}

	public enum Option {
		IGNORE_UNAVAILABLE, IGNORE_ALIASES, ALLOW_NO_INDICES, FORBID_ALIASES_TO_MULTIPLE_INDICES, FORBID_CLOSED_INDICES, IGNORE_THROTTLED;
	}
}
