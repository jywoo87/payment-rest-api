package com.jywoo.payment.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jywoo.payment.usecase.CancelUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class CancelResponseViewModel {

    @Setter @Getter
    @JsonProperty(value = "transaction_id", index = 1)
    private String transactionId;

    @Setter @Getter
    @JsonProperty(value = "body", index = 2)
    private String cancelString;

    public static CancelResponseViewModel from(CancelUseCase.CancelResponse response) {
        return CancelResponseViewModel.builder()
                .transactionId(response.getTransaction().getTransactionId())
                .cancelString(response.getPayRequestString())
                .build();
    }
}
