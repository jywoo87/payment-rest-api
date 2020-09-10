package com.jywoo.payment.except;

import com.jywoo.payment.except.message.ExceptionMessage;

public class ConcurrencyException extends AbstractRuntimeException {
    public ConcurrencyException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage);
    }
}
