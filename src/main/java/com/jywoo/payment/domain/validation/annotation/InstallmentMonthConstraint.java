package com.jywoo.payment.domain.validation.annotation;

import com.jywoo.payment.domain.validation.InstallmentMonthValidator;
import com.jywoo.payment.except.message.ValidationExceptionMessage;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InstallmentMonthValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InstallmentMonthConstraint {
    String message() default ValidationExceptionMessage.INVALID_PAY_INSTALLMENT_MONTH;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
