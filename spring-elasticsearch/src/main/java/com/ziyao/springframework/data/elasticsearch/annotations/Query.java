package com.ziyao.springframework.data.elasticsearch.annotations;
import com.ziyao.springframework.data.annotation.QueryAnnotation;

import java.lang.annotation.*;

/**
 * Query
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Peter-Josef Meisch
 * @author Steven Pearce
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Documented
@QueryAnnotation
public @interface Query {

	/**
	 * @return Elasticsearch query to be used when executing query. May contain placeholders eg. ?0
	 */
	String value() default "";

	/**
	 * Named Query Named looked up by repository.
	 *
	 * @deprecated since 4.2, not implemented and used anywhere
	 */
	String name() default "";

	/**
	 * Returns whether the query defined should be executed as count projection.
	 *
	 * @return {@literal false} by default.
	 * @since 4.2
	 */
	boolean count() default false;
}
