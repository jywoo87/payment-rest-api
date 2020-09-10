package com.jywoo.payment.client.template.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@AllArgsConstructor
@Getter
@ToString
public class TemplateFieldMeta<T extends Annotation> implements Comparable<TemplateFieldMeta<T>> {
    private Field field;
    private T templateField;

    @Override
    public int compareTo(TemplateFieldMeta<T> o) {
        return 0;
    }
}
