package com.jywoo.payment.usecase;

import java.util.Optional;

public abstract class AbstractPriceVatRequest {
    private Long price; // 결제금액
    private Optional<Long> vat = Optional.ofNullable(null); //vat

    public abstract Long getPrice();
    public abstract Long getVat();
}
