/*
 * Copyright 2012-2023 the original author or authors.
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
package org.ziyao.data.auditing.config;

import org.ziyao.data.mapping.context.MappingContext;
import org.w3c.dom.Element;
import org.ziyao.data.auditing.IsNewAwareAuditingHandler;

/**
 * {@link AuditingHandlerBeanDefinitionParser} that will register am {@link IsNewAwareAuditingHandler}. Needs to get the
 * bean id of the {@link MappingContext} it shall refer to.
 *
 * @author Oliver Gierke
 * @since 1.5
 */
public class IsNewAwareAuditingHandlerBeanDefinitionParser extends AuditingHandlerBeanDefinitionParser {

    /**
     * Creates a new {@link IsNewAwareAuditingHandlerBeanDefinitionParser}.
     *
     * @param mappingContextBeanName must not be {@literal null} or empty.
     */
    public IsNewAwareAuditingHandlerBeanDefinitionParser(String mappingContextBeanName) {
        super(mappingContextBeanName);
    }

    /*
     * (non-Javadoc)
     * @see org.ziyao.data.config.AuditingHandlerBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
     */
    @Override
    protected Class<?> getBeanClass(Element element) {
        return IsNewAwareAuditingHandler.class;
    }
}
