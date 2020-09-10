package com.jywoo.payment.client.template.annotations;

import com.jywoo.payment.client.template.annotations.enums.TemplateFormatterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateHeader {
    TemplateFormatterType type();
    int len();
}
