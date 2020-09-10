package com.jywoo.payment.except;

import com.jywoo.payment.except.message.ExceptionMessage;

public class TransactionNotFoundException extends AbstractRuntimeException {
    public TransactionNotFoundException() {
        super(ExceptionMessage.NOT_FOUND_TRANSACTION);
    }
}
