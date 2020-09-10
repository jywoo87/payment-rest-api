package com.jywoo.payment.domain;

import com.jywoo.payment.except.message.ValidationExceptionMessage;
import com.jywoo.payment.usecase.AbstractPriceVatRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Builder
@Embeddable
@ToString
public class PriceVat {
    public PriceVat() {}

    public PriceVat(Long price, Long vat) {
        this(price, vat, vat);
    }

    public PriceVat(Long price, Long vat, Long originVat) {
        this.price = price;
        this.vat = vat == null ? Math.round((double) price / 11) : vat;
        this.originVat = originVat;
    }

    @Getter
    @Min(value = 10, message = ValidationExceptionMessage.INVALID_PAY_PRICE_MIN)
    @Max(value = 1000000000, message = ValidationExceptionMessage.INVALID_PAY_PRICE_MAX)
    @Column(name = "PRICE")
    private Long price;

    @Getter
    @Column(name = "VAT")
    private Long vat;

    @Getter
    @Transient
    Long originVat;

    @Transient
    public boolean isAutoCalcVat() {
        return originVat == null && vat != null;
    }

    public static PriceVat add(PriceVat a, PriceVat b) {
        return PriceVat.builder()
                .price((a.getPrice() == null ? 0 : a.getPrice()) + (b.getPrice() == null ? 0 : b.getPrice()))
                .vat((a.getVat() == null ? 0 : a.getVat()) + (b.getVat() == null ? 0 : b.getVat()))
                .build();
    }

    public static PriceVat minus(PriceVat a, PriceVat b) {
        return PriceVat.builder()
                .price((a.getPrice() == null ? 0 : a.getPrice()) - (b.getPrice() == null ? 0 : b.getPrice()))
                .vat((a.getVat() == null ? 0 : a.getVat()) - (b.getVat() == null ? 0 : b.getVat()))
                .build();
    }

    public static PriceVat from(AbstractPriceVatRequest request) {
        return new PriceVat(request.getPrice(), request.getVat());
    }
}
