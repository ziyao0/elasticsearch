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
package org.ziyao.data.mapping.model;

import org.springframework.data.mapping.PersistentProperty;

/**
 * {@link FieldNamingStrategy} that abbreviates field names by using the very first letter of the camel case parts of
 * the {@link PersistentProperty}'s name.
 *
 * @author Oliver Gierke
 * @since 1.9
 */
public class CamelCaseAbbreviatingFieldNamingStrategy extends CamelCaseSplittingFieldNamingStrategy {

    /**
     * Creates a new {@link CamelCaseAbbreviatingFieldNamingStrategy}.
     */
    public CamelCaseAbbreviatingFieldNamingStrategy() {
        super("");
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.mongodb.core.mapping.CamelCaseSplittingFieldNamingStrategy#preparePart(java.lang.String)
     */
    @Override
    protected String preparePart(String part) {
        return part.substring(0, 1);
    }
}
