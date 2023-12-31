/*
 * Copyright2020-2021 the original author or authors.
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
package org.ziyao.data.elasticsearch.annotations;

import org.ziyao.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * Annotation to enable custom routing values for an entity.
 *
 * @author Peter-Josef Meisch
 * @since 4.2
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Routing {

    /**
     * defines how the routing is determined. Can be either the name of a property or a SpEL expression. See the reference
     * documentation for examples how to use this annotation.
     */
    String value();
}
