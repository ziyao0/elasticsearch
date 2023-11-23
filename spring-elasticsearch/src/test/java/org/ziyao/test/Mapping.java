package org.ziyao.test;

import org.ziyao.data.elasticsearch.core.EntityOperations;
import org.ziyao.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.ziyao.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.ziyao.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.ziyao.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.ziyao.data.mapping.context.MappingContext;

/**
 * @author ziyao zhang
 * @since 2023/11/23
 */
public class Mapping {


    public static void main(String[] args) {
        SimpleElasticsearchMappingContext simpleElasticsearchMappingContext = new SimpleElasticsearchMappingContext();
        MappingElasticsearchConverter mappingElasticsearchConverter = new MappingElasticsearchConverter(simpleElasticsearchMappingContext);
        mappingElasticsearchConverter.afterPropertiesSet();
        MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext = mappingElasticsearchConverter.getMappingContext();
        EntityOperations entityOperations1 = new EntityOperations(mappingContext);
        AuditLog auditLog = new AuditLog();
        auditLog.setId("aksal");
        auditLog.setLevel(2L);
        auditLog.setLog("zzzzzzzzz");
        EntityOperations.Entity<AuditLog> auditLogEntity = entityOperations1.forEntity(auditLog);
        Object id = auditLogEntity.getId();
        System.out.println(id);
    }
}
