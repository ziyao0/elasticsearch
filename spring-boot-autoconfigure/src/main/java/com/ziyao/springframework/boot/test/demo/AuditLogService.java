package com.ziyao.springframework.boot.test.demo;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.stereotype.Service;
import org.ziyao.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.ziyao.data.elasticsearch.core.EntityOperations;
import org.ziyao.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.ziyao.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.ziyao.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.ziyao.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author ziyao zhang
 * @since 2023/11/17
 */
@Service
public class AuditLogService {


    @Autowired
    private AuditLogRepository auditLogRepository;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @PostConstruct
    public void saveList() {
        ArrayList<AuditLog> auditLogs = Lists.newArrayList(
                new AuditLog("1", 0L, "新增操作"),
                new AuditLog("2", 1L, "新增操作1"),
                new AuditLog("3", 0L, "新增操作2"),
                new AuditLog("4", 2L, "新增操作3"),
                new AuditLog("5", 3L, "新增操作4"),
                new AuditLog("6", 2L, "新增操作5")
        );
        AuditLog auditLog = new AuditLog();
        auditLog.setId("1");
        auditLog.setLevel(2L);
        auditLog.setLog("zzzzzzzzz");
        AuditLog auditLogq = auditLogRepository.save(auditLog);
        auditLogRepository.saveAll(auditLogs);
//        Iterable<AuditLog> auditLogs1 = auditLogRepository.saveAll(auditLogs);
//        for (AuditLog auditLog : auditLogs1) {
//            System.out.println(auditLog);
//        }
    }

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
