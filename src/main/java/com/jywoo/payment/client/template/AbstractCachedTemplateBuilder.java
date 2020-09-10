package com.jywoo.payment.client.template;

import com.jywoo.payment.client.template.cache.TemplateFieldMeta;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractCachedTemplateBuilder<T extends Annotation> {
    private final Class<T> genericType;

    public AbstractCachedTemplateBuilder(Class<T> genericType) {
        this.genericType = genericType;
    }

    private final Map<Class<?>, List<TemplateFieldMeta>> cached = new HashMap<>();

    public String apply(Object instance) {
        final List<TemplateFieldMeta> templateFieldItems = getTemplate(instance.getClass());
        return buildTemplate(templateFieldItems, instance);
    }

    private List<TemplateFieldMeta> getTemplate(Class<?> clazz) {
        if(cached.containsKey(clazz)) {
            log.debug("-- hit cached : {}", clazz);
            return cached.get(clazz);
        }

        final List<TemplateFieldMeta> meta = getTemplateMetas(clazz, genericType);
        cached.put(clazz, meta);
        return meta;
    }

    private List<TemplateFieldMeta> getTemplateMetas(Class<?> clazz, Class<T> t) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(t))
                .map(field -> {
                    final T annotation = field.getDeclaredAnnotation(t);
                    return new TemplateFieldMeta<T>(field, annotation);
                })
                .sorted(getComparator() == null ? Comparator.naturalOrder() : getComparator())
                .collect(Collectors.toList());

    }

    private String buildTemplate(List<TemplateFieldMeta> fields, Object instance) {
        return fields.stream()
                .map(fieldMeta -> buildRow(fieldMeta, instance))
                .collect(Collectors.joining())
                .toString();
    }


    protected Object getValue(Field field, Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            log.error("", e);
        }

        throw new RuntimeException("PARAMETER TEMPLATE ERROR");
    }

    public abstract String buildRow(TemplateFieldMeta<T> fieldMeta, Object instance);

    public abstract Comparator<TemplateFieldMeta<T>> getComparator();
}
