package org.springframework.data.annotation;

import org.springframework.core.DecoratingProxy;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;

/**
 * @author ziyao zhang
 * @since 2023/11/20
 */
public class AnnotationAwareOrderComparator extends OrderComparator {
    public static final org.springframework.core.annotation.AnnotationAwareOrderComparator INSTANCE = new org.springframework.core.annotation.AnnotationAwareOrderComparator();

    public AnnotationAwareOrderComparator() {
    }

    @Nullable
    protected Integer findOrder(Object obj) {
        Integer order = super.findOrder(obj);
        return order != null ? order : this.findOrderFromAnnotation(obj);
    }

    @Nullable
    private Integer findOrderFromAnnotation(Object obj) {
        AnnotatedElement element = obj instanceof AnnotatedElement ? (AnnotatedElement)obj : obj.getClass();
        MergedAnnotations annotations = MergedAnnotations.from((AnnotatedElement)element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
        Integer order = org.springframework.data.annotation.OrderUtils.getOrderFromAnnotations((AnnotatedElement)element, annotations);
        return order == null && obj instanceof DecoratingProxy ? this.findOrderFromAnnotation(((DecoratingProxy)obj).getDecoratedClass()) : order;
    }

    @Nullable
    public Integer getPriority(Object obj) {
        if (obj instanceof Class) {
            return OrderUtils.getPriority((Class)obj);
        } else {
            Integer priority = OrderUtils.getPriority(obj.getClass());
            return priority == null && obj instanceof DecoratingProxy ? this.getPriority(((DecoratingProxy)obj).getDecoratedClass()) : priority;
        }
    }

    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }

    }

    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }

    }

    public static void sortIfNecessary(Object value) {
        if (value instanceof Object[]) {
            sort((Object[])((Object[])value));
        } else if (value instanceof List) {
            sort((List)value);
        }

    }
}
