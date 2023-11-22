/*
 * Copyright 2015-2023 the original author or authors.
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
package org.ziyao.data.projection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.ziyao.data.log.LogMessage;
import org.ziyao.data.type.MethodsMetadata;
import org.ziyao.data.type.classreading.MethodsMetadataReader;
import org.ziyao.data.type.classreading.MethodsMetadataReaderFactory;
import org.ziyao.data.util.StreamUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Default implementation of {@link ProjectionInformation}. Exposes all properties of the type as required input
 * properties.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Johannes Englmeier
 * @since 1.12
 */
class DefaultProjectionInformation implements ProjectionInformation {

    private final Class<?> projectionType;
    private final List<PropertyDescriptor> properties;

    /**
     * Creates a new {@link DefaultProjectionInformation} for the given type.
     *
     * @param type must not be {@literal null}.
     */
    DefaultProjectionInformation(Class<?> type) {

        Assert.notNull(type, "Projection type must not be null");

        this.projectionType = type;
        this.properties = new PropertyDescriptorSource(type).getDescriptors();
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.projection.ProjectionInformation#getType()
     */
    @Override
    public Class<?> getType() {
        return projectionType;
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.projection.ProjectionInformation#getInputProperties()
     */
    public List<PropertyDescriptor> getInputProperties() {

        return properties.stream()//
                .filter(this::isInputProperty)//
                .distinct()//
                .collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.projection.ProjectionInformation#isDynamic()
     */
    @Override
    public boolean isClosed() {
        return this.properties.equals(getInputProperties());
    }

    /**
     * Returns whether the given {@link PropertyDescriptor} describes an input property for the projection, i.e. a
     * property that needs to be present on the source to be able to create reasonable projections for the type the
     * descriptor was looked up on.
     *
     * @param descriptor will never be {@literal null}.
     * @return
     */
    protected boolean isInputProperty(PropertyDescriptor descriptor) {
        return true;
    }

    /**
     * Returns whether the given {@link PropertyDescriptor} has a getter that is a Java 8 default method.
     *
     * @param descriptor must not be {@literal null}.
     * @return
     */
    private static boolean hasDefaultGetter(PropertyDescriptor descriptor) {

        Method method = descriptor.getReadMethod();

        return method != null && method.isDefault();
    }

    /**
     * Internal helper to detect {@link PropertyDescriptor} instances for a given type.
     *
     * @author Mark Paluch
     * @author Oliver Gierke
     * @soundtrack The Meters - Cissy Strut (Here Comes The Meter Man)
     * @since 2.1
     */
    private static class PropertyDescriptorSource {

        private static final Log logger = LogFactory.getLog(PropertyDescriptorSource.class);

        private final Class<?> type;
        private final Optional<MethodsMetadata> metadata;

        /**
         * Creates a new {@link PropertyDescriptorSource} for the given type.
         *
         * @param type must not be {@literal null}.
         */
        PropertyDescriptorSource(Class<?> type) {

            Assert.notNull(type, "Type must not be null");

            this.type = type;
            this.metadata = getMetadata(type);
        }

        /**
         * Returns {@link PropertyDescriptor}s for all properties exposed by the given type and all its super interfaces.
         *
         * @return
         */
        List<PropertyDescriptor> getDescriptors() {
            return collectDescriptors().distinct().collect(StreamUtils.toUnmodifiableList());
        }

        /**
         * Recursively collects {@link PropertyDescriptor}s for all properties exposed by the given type and all its super
         * interfaces.
         *
         * @return
         */
        private Stream<PropertyDescriptor> collectDescriptors() {

            Stream<PropertyDescriptor> allButDefaultGetters = Arrays.stream(BeanUtils.getPropertyDescriptors(type)) //
                    .filter(it -> !hasDefaultGetter(it));

            Stream<PropertyDescriptor> ownDescriptors = metadata.map(it -> filterAndOrder(allButDefaultGetters, it)) //
                    .orElse(allButDefaultGetters);

            Stream<PropertyDescriptor> superTypeDescriptors = metadata.map(this::fromMetadata) //
                    .orElseGet(this::fromType) //
                    .flatMap(it -> new PropertyDescriptorSource(it).collectDescriptors());

            return Stream.concat(ownDescriptors, superTypeDescriptors);
        }

        /**
         * Returns a {@link Stream} of {@link PropertyDescriptor} ordered following the given {@link MethodsMetadata} only
         * returning methods seen by the given {@link MethodsMetadata}.
         *
         * @param source   must not be {@literal null}.
         * @param metadata must not be {@literal null}.
         * @return
         */
        private Stream<PropertyDescriptor> filterAndOrder(Stream<PropertyDescriptor> source, MethodsMetadata metadata) {

            Map<String, Integer> orderedMethods = getMethodOrder(metadata);

            Stream<PropertyDescriptor> filtered = source.filter(it -> it.getReadMethod() != null)
                    .filter(it -> it.getReadMethod().getDeclaringClass().equals(type));

            return orderedMethods.isEmpty()
                    ? filtered
                    : filtered.sorted(Comparator.comparingInt(left -> orderedMethods.get(left.getReadMethod().getName())));
        }

        /**
         * Returns a {@link Stream} of interfaces using the given {@link MethodsMetadata} as primary source for ordering.
         *
         * @param metadata must not be {@literal null}.
         * @return
         */
        private Stream<Class<?>> fromMetadata(MethodsMetadata metadata) {
            return Arrays.stream(metadata.getInterfaceNames()).map(it -> findType(it, type.getInterfaces()));
        }

        /**
         * Returns a {@link Stream} of interfaces using the given type as primary source for ordering.
         *
         * @return
         */
        private Stream<Class<?>> fromType() {
            return Arrays.stream(type.getInterfaces());
        }

        /**
         * Attempts to obtain {@link MethodsMetadata} from {@link Class}. Returns {@link Optional} containing
         * {@link MethodsMetadata} if metadata was read successfully, {@link Optional#empty()} otherwise.
         *
         * @param type must not be {@literal null}.
         * @return the optional {@link MethodsMetadata}.
         */
        private static Optional<MethodsMetadata> getMetadata(Class<?> type) {

            try {

                MethodsMetadataReaderFactory factory = new MethodsMetadataReaderFactory(type.getClassLoader());
                MethodsMetadataReader metadataReader = factory.getMetadataReader(ClassUtils.getQualifiedName(type));

                return Optional.of(metadataReader.getMethodsMetadata());

            } catch (IOException e) {

                logger.info(
                        LogMessage.format("Couldn't read class metadata for %s. Input property calculation might fail", type));
                return Optional.empty();
            }
        }

        /**
         * Find the type with the given name in the given array of {@link Class}.
         *
         * @param name  must not be {@literal null} or empty.
         * @param types must not be {@literal null}.
         * @return
         */
        private static Class<?> findType(String name, Class<?>[] types) {

            return Arrays.stream(types) //
                    .filter(it -> name.equals(it.getName())) //
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Did not find type %s in %s", name, Arrays.toString(types))));
        }

        /**
         * Returns a {@link Map} containing method name to its positional index according to {@link MethodsMetadata}.
         *
         * @param metadata
         * @return
         */
        private static Map<String, Integer> getMethodOrder(MethodsMetadata metadata) {

            List<String> methods = metadata.getMethods() //
                    .stream() //
                    .map(MethodMetadata::getMethodName) //
                    .distinct() //
                    .collect(Collectors.toList());

            return IntStream.range(0, methods.size()) //
                    .boxed() //
                    .collect(Collectors.toMap(methods::get, i -> i));
        }
    }
}
