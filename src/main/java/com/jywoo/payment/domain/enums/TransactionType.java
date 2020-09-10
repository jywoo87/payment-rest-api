package com.jywoo.payment.domain.enums;

import lombok.Getter;

public enum TransactionType {
    PAYMENT("결제"), CANCEL("결제취소");

    @Getter
    private String description;

    TransactionType(String description) {
        this.description = description;
    }
}
