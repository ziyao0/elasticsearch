package com.ziyao.springframework.boot.test.demo;

import org.ziyao.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ziyao zhang
 * @since 2023/11/17
 */
@Repository
public interface AuditLogRepository extends ElasticsearchRepository<AuditLog, String> {
}
