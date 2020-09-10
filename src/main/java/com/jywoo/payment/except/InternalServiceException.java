package com.jywoo.payment.except;

import com.jywoo.payment.except.message.ExceptionMessage;

public class InternalServiceException extends AbstractRuntimeException {
    public InternalServiceException() {
        super(ExceptionMessage.INTERNAL_SERVICE_ERROR);
    }

    public InternalServiceException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage);
    }
}
