package com.jywoo.payment.domain;

import com.jywoo.payment.domain.supported.converter.CardInfoConverter;
import com.jywoo.payment.domain.validation.annotation.InstallmentMonthConstraint;
import com.jywoo.payment.usecase.PaymentUseCase;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;

@Entity
@Table(name = "PAYMENT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private Long paymentId;

    @Convert(converter = CardInfoConverter.class)
    @Column(name = "ENCRYPTED_CARD_INFO", length = 100)
    @Valid
    private CardInfo cardInfo;

    @Column(name = "INSTALLMENT_MONTH")
    @InstallmentMonthConstraint
    private int installmentMonth;

    public static Payment from(PaymentUseCase.PaymentRequest paymentRequest) {
        final Payment payment = new Payment();
        payment.setCardInfo(CardInfo.from(paymentRequest));
        payment.setInstallmentMonth(paymentRequest.getInstallmentMonth());

        return payment;
    }
}
