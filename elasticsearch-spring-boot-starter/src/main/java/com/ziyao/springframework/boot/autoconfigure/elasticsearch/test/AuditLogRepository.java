package com.ziyao.springframework.boot.autoconfigure.elasticsearch.test;

import org.springframework.stereotype.Repository;
import org.ziyao.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author ziyao zhang
 * @since 2023/11/17
 */
@Repository
public interface AuditLogRepository extends ElasticsearchRepository<AuditLog, String> {
}
