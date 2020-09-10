package com.jywoo.payment.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jywoo.payment.usecase.PaymentUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class PaymentResponseViewModel {

    @Setter @Getter
    @JsonProperty(value = "transaction_id", index = 1)
    private String transactionId;

    @Setter @Getter
    @JsonProperty(value = "body", index = 2)
    private String paymentString;

    public static PaymentResponseViewModel from(PaymentUseCase.PaymentResponse response) {
        return PaymentResponseViewModel.builder()
                .transactionId(response.getTransaction().getTransactionId())
                .paymentString(response.getPayRequestString())
                .build();
    }
}
