package com.jywoo.payment.lock.annotations;

import com.jywoo.payment.domain.enums.Operation;
import com.jywoo.payment.except.message.ExceptionMessage;
import jdk.nashorn.internal.objects.annotations.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLock {
    @Getter
    Operation operation();

    @Getter
    String expression();

    @Getter
    ExceptionMessage exceptionMessage() default ExceptionMessage.INTERNAL_SERVICE_ERROR;
}
