package com.jywoo.payment.domain.validation.annotation;

import com.jywoo.payment.domain.validation.CardExpireDateValidator;
import com.jywoo.payment.except.message.ValidationExceptionMessage;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CardExpireDateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CardExpireDateConstraint {
    String message() default ValidationExceptionMessage.INVALID_CARD_EXP_DATE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
