package org.ziyao.data.elasticsearch.annotations;


import org.ziyao.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * Elasticsearch dynamic templates mapping. This annotation is handy if you prefer apply dynamic templates on fields
 * with annotation e.g. {@link Field} with type = FieldType.Object etc. instead of static mapping on Document via
 * {@link Mapping} annotation. DynamicTemplates annotation is omitted if {@link Mapping} annotation is used.
 *
 * @author Petr Kukral
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DynamicTemplates {

    String mappingPath() default "";
}
