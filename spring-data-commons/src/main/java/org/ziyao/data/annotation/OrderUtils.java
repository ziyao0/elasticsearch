package org.ziyao.data.annotation;

import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

/**
 * @author ziyao zhang
 * @since 2023/11/20
 */
public abstract class OrderUtils {
    private static final Object NOT_ANNOTATED = new Object();
    private static final String JAVAX_PRIORITY_ANNOTATION = "javax.annotation.Priority";
    static final Map<AnnotatedElement, Object> orderCache = new ConcurrentReferenceHashMap(64);

    public OrderUtils() {
    }

    public static int getOrder(Class<?> type, int defaultOrder) {
        Integer order = getOrder(type);
        return order != null ? order : defaultOrder;
    }

    @Nullable
    public static Integer getOrder(Class<?> type, @Nullable Integer defaultOrder) {
        Integer order = getOrder(type);
        return order != null ? order : defaultOrder;
    }

    @Nullable
    public static Integer getOrder(Class<?> type) {
        return getOrder((AnnotatedElement) type);
    }

    @Nullable
    public static Integer getOrder(AnnotatedElement element) {
        return getOrderFromAnnotations(element, MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY));
    }

    @Nullable
    static Integer getOrderFromAnnotations(AnnotatedElement element, MergedAnnotations annotations) {
        if (!(element instanceof Class)) {
            return findOrder(annotations);
        } else {
            Object cached = orderCache.get(element);
            if (cached != null) {
                return cached instanceof Integer ? (Integer) cached : null;
            } else {
                Integer result = findOrder(annotations);
                orderCache.put(element, result != null ? result : NOT_ANNOTATED);
                return result;
            }
        }
    }

    @Nullable
    private static Integer findOrder(MergedAnnotations annotations) {
        MergedAnnotation<org.springframework.core.annotation.Order> orderAnnotation = annotations.get(Order.class);
        if (orderAnnotation.isPresent()) {
            return orderAnnotation.getInt("value");
        } else {
            MergedAnnotation<?> priorityAnnotation = annotations.get("javax.annotation.Priority");
            return priorityAnnotation.isPresent() ? priorityAnnotation.getInt("value") : null;
        }
    }
}
