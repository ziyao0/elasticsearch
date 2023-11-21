package com.ziyao.springframework.boot.test.demo;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author ziyao zhang
 * @since 2023/11/17
 */
@Service
public class AuditLogService {


    @Autowired
    private AuditLogRepository auditLogRepository;

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
        AuditLog auditLogq = auditLogRepository.save(new AuditLog("1", 0L, "新增操作"));
        System.out.println(auditLogq);
        Iterable<AuditLog> auditLogs1 = auditLogRepository.saveAll(auditLogs);
        for (AuditLog auditLog : auditLogs1) {
            System.out.println(auditLog);
        }
    }
}
