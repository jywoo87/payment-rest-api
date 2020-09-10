package com.jywoo.payment.domain.validation.annotation;

import com.jywoo.payment.domain.validation.PriceVatValidator;
import com.jywoo.payment.except.message.ValidationExceptionMessage;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PriceVatValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PriceVatConstraint {
    String message() default ValidationExceptionMessage.INVALID_PAY_VAT_BIGGER_THAN_PRICE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String priceFieldName() default "";
    String vatFieldName() default "";


    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        PriceVatConstraint[] value();
    }
}
