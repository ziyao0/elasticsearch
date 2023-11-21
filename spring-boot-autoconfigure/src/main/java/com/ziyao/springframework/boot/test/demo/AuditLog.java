package com.ziyao.springframework.boot.test.demo;

import com.ziyao.springframework.data.elasticsearch.annotations.Document;
import com.ziyao.springframework.data.elasticsearch.annotations.Field;
import com.ziyao.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author ziyao zhang
 * @since 2023/11/17
 */
@Document(indexName = "audit_log_index")
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1370345136603086743L;
    @Field
    private String id;
    @Field(type = FieldType.Long)
    private Long level;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
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
