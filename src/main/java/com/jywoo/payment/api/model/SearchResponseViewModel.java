package com.jywoo.payment.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jywoo.payment.api.serializer.MaskingCardNoSerializer;
import com.jywoo.payment.domain.CardInfo;
import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.domain.enums.TransactionType;
import com.jywoo.payment.usecase.SearchUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class SearchResponseViewModel {

    @Setter @Getter
    @JsonProperty(value = "transaction_id", index = 1)
    private String transactionId;

    @Setter @Getter
    @JsonProperty(value = "card", index = 3)
    private CardInfoModel cardInfoModel;

    @Setter @Getter
    @JsonProperty(value = "trancaction_type", index = 2)
    private TransactionType transactionType;

    @Setter @Getter
    @JsonProperty(value = "payment", index = 4)
    private PriceVatModel priceVatModel;

    public static SearchResponseViewModel from(SearchUseCase.SearchResponse response) {
        final Transaction transaction = response.getTransaction();
        return SearchResponseViewModel.builder()
                .transactionId(transaction.getTransactionId())
                .transactionType(transaction.getTransactionType())
                .cardInfoModel(CardInfoModel.from(transaction.getPayment().getCardInfo()))
                .priceVatModel(PriceVatModel.from(transaction.getPriceVat()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class CardInfoModel {
        @JsonProperty(value = "no", index = 1)
        @JsonSerialize(using = MaskingCardNoSerializer.class)
        private String no;

        @JsonProperty(value = "exp", index = 2)
        private String exp;

        @JsonProperty(value = "cvc", index = 3)
        private String cvc;

        public static CardInfoModel from(CardInfo cardInfo) {
            return new CardInfoModel(cardInfo.getCardNo(), cardInfo.getExpDate(), String.valueOf(cardInfo.getCvc()));
        }
    }

    @Getter
    @AllArgsConstructor
    public static class PriceVatModel {
        @JsonProperty(value = "price", index = 1)
        private Long price;

        @JsonProperty(value = "vat", index = 2)
        private Long vat;

        public static PriceVatModel from(PriceVat priceVat) {
            return new PriceVatModel(priceVat.getPrice(), priceVat.getVat());
        }
    }
}
