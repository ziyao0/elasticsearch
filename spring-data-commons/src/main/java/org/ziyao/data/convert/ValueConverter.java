/*
 * Copyright 2022-2023 the original author or authors.
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
package org.ziyao.data.convert;

import org.ziyao.data.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;

/**
 * Annotation to define usage of a {@link PropertyValueConverter} to read/write the property. <br />
 * Can be used as meta annotation utilizing {@link AliasFor}.
 * <p>
 * The target {@link PropertyValueConverter} is typically provided via a {@link PropertyValueConverterFactory converter
 * factory}.
 * <p>
 * Consult the store-specific documentation for details and support notes.
 *
 * @author Christoph Strobl
 * @see PropertyValueConverter
 * @see PropertyValueConverterFactory
 * @since 2.7
 */
@Target({FIELD, ANNOTATION_TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueConverter {

    /**
     * The {@link PropertyValueConverter} type handling the value conversion of the annotated property.
     *
     * @return the configured {@link PropertyValueConverter}. {@link PropertyValueConverter.ObjectToObjectPropertyValueConverter} by default.
     */
    Class<? extends PropertyValueConverter> value() default PropertyValueConverter.ObjectToObjectPropertyValueConverter.class;

}
