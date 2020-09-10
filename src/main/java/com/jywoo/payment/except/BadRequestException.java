package com.jywoo.payment.except;

import com.jywoo.payment.except.message.ExceptionMessage;

public class BadRequestException extends AbstractRuntimeException {
    public BadRequestException() {
        super(ExceptionMessage.BAD_REQUEST);
    }

    public BadRequestException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage);
    }
}
