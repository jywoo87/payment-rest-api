package com.jywoo.payment.domain;

import com.jywoo.payment.domain.validation.annotation.CardExpireDateConstraint;
import com.jywoo.payment.except.message.ValidationExceptionMessage;
import com.jywoo.payment.usecase.PaymentUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class CardInfo {
    @NotNull
    @Pattern(regexp = "[0-9]{10,16}", message = ValidationExceptionMessage.INVALID_CARD_NO)
    private String cardNo;

    @NotNull
    @CardExpireDateConstraint
    private String expDate;

    @Pattern(regexp = "[0-9]{3}", message = ValidationExceptionMessage.INVALID_CARD_CVC)
    private String cvc;

    public static CardInfo from(PaymentUseCase.PaymentRequest paymentRequestModel) {
        return CardInfo.builder()
                .cardNo(paymentRequestModel.getCardNo())
                .expDate(paymentRequestModel.getExpDate())
                .cvc(paymentRequestModel.getCvc())
                .build();
    }
}