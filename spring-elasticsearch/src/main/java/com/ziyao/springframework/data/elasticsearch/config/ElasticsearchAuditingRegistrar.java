/*
 * Copyright 2020-2023 the original author or authors.
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
package com.ziyao.springframework.data.elasticsearch.config;

import com.ziyao.springframework.data.elasticsearch.core.event.AuditingEntityCallback;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import com.ziyao.springframework.data.auditing.IsNewAwareAuditingHandler;
import com.ziyao.springframework.data.auditing.config.AuditingBeanDefinitionRegistrarSupport;
import com.ziyao.springframework.data.auditing.config.AuditingConfiguration;
import com.ziyao.springframework.data.config.ParsingUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * {@link ImportBeanDefinitionRegistrar} to enable {@link EnableElasticsearchAuditing} annotation.
 *
 * @author Peter-Josef Meisch
 * @since 4.0
 */
class ElasticsearchAuditingRegistrar extends AuditingBeanDefinitionRegistrarSupport {

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableElasticsearchAuditing.class;
	}

	@Override
	protected String getAuditingHandlerBeanName() {
		return "elasticsearchAuditingHandler";
	}

	@Override
	protected BeanDefinitionBuilder getAuditHandlerBeanDefinitionBuilder(AuditingConfiguration configuration) {

		Assert.notNull(configuration, "AuditingConfiguration must not be null!");

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(IsNewAwareAuditingHandler.class);

		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(PersistentEntitiesFactoryBean.class);
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		builder.addConstructorArgValue(definition.getBeanDefinition());
		return configureDefaultAuditHandlerAttributes(configuration, builder);
	}

	@Override
	protected void registerAuditListenerBeanDefinition(BeanDefinition auditingHandlerDefinition,
			BeanDefinitionRegistry registry) {

		Assert.notNull(auditingHandlerDefinition, "BeanDefinition must not be null!");
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(AuditingEntityCallback.class);
		builder.addConstructorArgValue(ParsingUtils.getObjectFactoryBeanDefinition(getAuditingHandlerBeanName(), registry));

		registerInfrastructureBeanWithId(builder.getBeanDefinition(), AuditingEntityCallback.class.getName(), registry);
	}
}
