package com.jywoo.payment.except.message;

import lombok.Getter;

public enum ExceptionMessage {
    NOT_ENOUGH_PRICE(2002, "남은 결제금액이 모자르거나 없습니다."),
    NOT_ENOUGH_VAT(2003, "남은 부가가치세금액 모자르거나 없습니다."),
    REMAIN_VAT(2004, "남은 부가가치세가 있습니다."),

    BAD_REQUEST(4000, "잘못된 요청입니다."),
    INVALID_PAYMENT_TRANSACTION_ID(4001, "요청하신 관리번호는 결제 관리번호가 아닙니다."),
    NOT_FOUND_TRANSACTION(4004, "트랜잭션 정보를 찾을 수 없습니다."),
    CONCURRENCY_EXCEPTION_CARD_NO(4090, "하나의 카드 번호로 여러 개의 요청이 진행 중 입니다."),
    CONCURRENCY_EXCEPTION_TRANSACTION(4091, "하나의 결제 건에 대해 다수 건의 취소 요청이 진행 중 입니다."),

    INTERNAL_SERVICE_ERROR(5000, "알 수 없는 에러가 발생했습니다. 잠시 후 다시 시도해주세요."),
    INTERNAL_TEMPLATE_ERROR(5001, "알 수 없는 에러 (템플릿 오류)"),
    INTERNAL_NOT_INITIALIZED_METHOD(5002, "알 수 없는 에러 (정의되지 않은 메서드)");

    @Getter
    private long code;

    @Getter
    private String message;

    ExceptionMessage(long code, String message) {
        this.code = code;
        this.message = message;
    }
}
