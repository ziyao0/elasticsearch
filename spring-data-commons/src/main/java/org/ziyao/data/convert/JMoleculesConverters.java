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
package org.ziyao.data.convert;

import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Registers jMolecules converter implementations with {@link CustomConversions} if the former is on the classpath.
 *
 * @author Oliver Drotbohm
 * @since 2.5
 */
public class JMoleculesConverters {

    private static final boolean JMOLECULES_PRESENT = ClassUtils.isPresent(
            "org.jmolecules.spring.IdentifierToPrimitivesConverter",
            JMoleculesConverters.class.getClassLoader());

    /**
     * Returns all jMolecules-specific converters to be registered.
     *
     * @return will never be {@literal null}.
     */
    public static Collection<Object> getConvertersToRegister() {

        if (!JMOLECULES_PRESENT) {
            return Collections.emptyList();
        }

        List<Object> converters = new ArrayList<>();
// TODO: 2023/11/23 类型转换 
//        Supplier<ConversionService> conversionService = () -> DefaultConversionService.getSharedInstance();
//
//        IdentifierToPrimitivesConverter toPrimitives = new IdentifierToPrimitivesConverter(conversionService);
//        PrimitivesToIdentifierConverter toIdentifier = new PrimitivesToIdentifierConverter(conversionService);
//
//        converters.add(toPrimitives);
//        converters.add(toIdentifier);
//        converters.add(new AssociationToPrimitivesConverter<>(toPrimitives));
//        converters.add(new PrimitivesToAssociationConverter<>(toIdentifier));

        return converters;
    }
}
