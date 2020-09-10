package com.jywoo.payment.except;

import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.except.message.ExceptionMessage;
import lombok.Getter;

public class NotEnoughVatException extends AbstractRuntimeException {
    public NotEnoughVatException() {
        super(ExceptionMessage.NOT_ENOUGH_VAT);
    }

    @Getter
    private Transaction transaction;

    @Getter
    private PriceVat remainPriceVat;

    @Getter
    private PriceVat priceVat;

    public NotEnoughVatException(Transaction transaction, PriceVat remainPriceVat, PriceVat priceVat) {
        super(ExceptionMessage.NOT_ENOUGH_PRICE, format(remainPriceVat, priceVat));

        this.transaction = transaction;
        this.remainPriceVat = remainPriceVat;
        this.priceVat = priceVat;
    }

    private static String format(PriceVat remainPriceVat, PriceVat priceVat) {
        return String.format("%,d(%,d)원 취소하려 했으나 남은 부가가치세(%,d)보다 취소요청 부가가치세(%,d)가 커서 실패", priceVat.getPrice(), priceVat.getVat(), remainPriceVat.getVat(), priceVat.getVat());
    }
}
