package com.jywoo.payment.client.template.annotations;

import com.jywoo.payment.client.template.annotations.enums.TemplateFormatterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateField {
    TemplateFormatterType formatter();
    int len();
    int ordered() default Integer.MAX_VALUE;
    boolean nullable() default false;
}
