package com.jywoo.payment.except;

import com.jywoo.payment.except.message.ExceptionMessage;
import lombok.Getter;

@Getter
public class AbstractRuntimeException extends RuntimeException {
    private ExceptionMessage exceptionMessage;

    public AbstractRuntimeException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.getMessage());
        this.exceptionMessage = exceptionMessage;
    }

    public AbstractRuntimeException(ExceptionMessage exceptionMessage, String message) {
        super(message);
        this.exceptionMessage = exceptionMessage;
    }

    public ExceptionMessage getExceptionMessage() {
        return this.exceptionMessage;
    }
}
