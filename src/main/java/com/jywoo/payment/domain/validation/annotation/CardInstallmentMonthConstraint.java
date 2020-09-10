package com.jywoo.payment.domain.validation.annotation;

import com.jywoo.payment.domain.validation.CardExpireDateValidator;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CardExpireDateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CardInstallmentMonthConstraint {
    String message() default "Invalid Card Expire Date";
}
