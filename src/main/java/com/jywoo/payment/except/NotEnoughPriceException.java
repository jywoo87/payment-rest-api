package com.jywoo.payment.except;

import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.except.message.ExceptionMessage;
import lombok.Getter;

public class NotEnoughPriceException extends AbstractRuntimeException {

    public NotEnoughPriceException() {
        super(ExceptionMessage.NOT_ENOUGH_PRICE);
    }

    @Getter
    private Transaction transaction;

    @Getter
    private PriceVat remainPriceVat;

    @Getter
    private PriceVat priceVat;

    public NotEnoughPriceException(Transaction transaction, PriceVat remainPriceVat, PriceVat priceVat) {
        super(ExceptionMessage.NOT_ENOUGH_PRICE, format(remainPriceVat, priceVat));

        this.transaction = transaction;
        this.remainPriceVat = remainPriceVat;
        this.priceVat = priceVat;
    }

    private static String format(PriceVat remainPriceVat, PriceVat priceVat) {
        if(remainPriceVat.getPrice() == 0L) {
            return String.format("%,d원 취소하려 했으나 남은 결제금액이 없어서 실패", priceVat.getPrice());
        }
        return String.format("%,d원 취소하려 했으나, 남은 결제금액(%,d)보다 커서 실패", priceVat.getPrice(), remainPriceVat.getPrice());
    }
}
