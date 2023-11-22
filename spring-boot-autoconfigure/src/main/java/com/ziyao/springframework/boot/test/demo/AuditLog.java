package com.ziyao.springframework.boot.test.demo;

import org.ziyao.data.annotation.Id;
import org.ziyao.data.elasticsearch.annotations.Document;
import org.ziyao.data.elasticsearch.annotations.Field;
import org.ziyao.data.elasticsearch.annotations.FieldType;
import org.ziyao.data.elasticsearch.annotations.Setting;

/**
 * @author ziyao zhang
 * @since 2023/11/17
 */
@Setting(shards = 2, replicas = 2)
@Document(indexName = "audit_log_index")
public class AuditLog {

    @Id
    private String id;
    @Field(type = FieldType.Long)
    private Long level;
    @Field(type = FieldType.Text)
    private String log;

    public AuditLog(String id, Long level, String log) {
        this.id = id;
        this.level = level;
        this.log = log;
    }

    public AuditLog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
