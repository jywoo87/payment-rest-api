package com.jywoo.payment.domain.validation.annotation;

import com.jywoo.payment.domain.validation.PayClientTransactionIdValidator;
import com.jywoo.payment.except.message.ValidationExceptionMessage;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PayClientTransactionIdValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RUNTIME)
public @interface PayClientTransactionIdConstraint {
    String message() default ValidationExceptionMessage.INVALID_PAY_CLIENT_TEMPLATE_EMPTY_TRANSACTION_ID;
    String transactionTypeField();
    String transactionIdField();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        PayClientTransactionIdConstraint[] value();
    }
}
