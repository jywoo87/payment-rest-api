package com.jywoo.payment.except.message;

import lombok.Getter;

@Getter
public class ValidationExceptionMessage {
    public static final String REQUIRED_CARD_EXP_DATE = "카드 만료일은 입력 필수 값 입니다.";
    public static final String REQUIRED_CARD_NO = "카드번호는 입력 필수 값 입니다.";
    public static final String REQUIRED_CARD_CVC = "카드 CVC는 필수 값 입니다.";
    public static final String REQUIRED_INSTALLMENT_MONTH = "할부개월수는 필수 값 입니다.";
    public static final String REQUIRED_PAYMENT_PRICE = "결제금액은 입력 필수 값 입니다.";

    public static final String REQUIRED_TRANSACTION_ID = "관리번호는 입력 필수 값 입니다.";

    public static final String INVALID_CARD_CVC = "CVC 정보가 잘못 입력되었습니다.";
    public static final String INVALID_CARD_NO = "카드 정보가 잘못 입력되었습니다. (길이 10~16, 숫자, 하이픈(-) 제외)";
    public static final String INVALID_CARD_EXP_DATE = "카드 만료일(MMYY) 정보를 확인해주세요.";
    public static final String INVALID_PAY_PRICE_MIN = "최소 결제금액은 10원 입니다.";
    public static final String INVALID_PAY_PRICE_MAX = "최대 결제금액은 10억원 이상입니다.";
    public static final String INVALID_PAY_INSTALLMENT_MONTH = "할부 기간은 일시불(0) 혹은 2~12개월만 가능합니다.";
    public static final String INVALID_PAY_VAT_BIGGER_THAN_PRICE = "부가가치세가 거래금액보다 큽니다.";

    public static final String INVALID_TRANSACTION_ID = "관리번호는 {min}~{max}자로 입력되어야 합니다.";

    public static final String INVALID_PAY_CLIENT_TEMPLATE_EMPTY_TRANSACTION_ID = "(카드사) 카드사 전송 값에 TRANSACTION ID 필드가 공백입니다.";
    public static final String PAYCLIENT_INVALID_PAY_PRICE_MIN = "최소 결제금액은 100원 입니다.";
}
