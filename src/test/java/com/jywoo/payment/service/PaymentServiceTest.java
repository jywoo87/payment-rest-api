package com.jywoo.payment.service;

import com.jywoo.payment.domain.CardInfo;
import com.jywoo.payment.domain.Payment;
import com.jywoo.payment.domain.PriceVat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Disabled
class PaymentServiceTest {
    private final PaymentService paymentService;

    @Test
    @DisplayName("결제 생성")
    void createPayment() {
        final PriceVat priceVat = new PriceVat(1000L, 91L);
        Payment payment = paymentService.create(dummyPayment(priceVat));
        log.debug("-- payment : {}", payment);

        assertNotNull(payment.getPaymentId());
    }

//    Payment에서 해당 정보 제거했음
//
//    @Test
//    @DisplayName("결제 생성 _ 잘못된 부가세")
//    void when_잘못된_부가세() {
//        final PriceVat priceVat = new PriceVat(1000L, 1200L);
//        final Payment payment = dummyPayment(priceVat);
//
//        assertThrows(ConstraintViolationException.class, () -> paymentService.create(payment));
//    }

    @Test
    @DisplayName("결제 생성 _ 잘못된 카드_유효기간오류")
    void when_잘못된_카드정보_유효기간오류() {
        final PriceVat priceVat = new PriceVat(1000L, 91L);
        final Payment payment = dummyPayment(priceVat);
        final CardInfo cardInfo = payment.getCardInfo();
        payment.setCardInfo(new CardInfo(cardInfo.getCardNo(), "2022", "123"));

        assertThrows(ConstraintViolationException.class, () -> paymentService.create(payment));
    }

    @Test
    @DisplayName("결제 생성 _ 잘못된 카드_짧은_카드번호")
    void when_잘못된_카드정보_짧은_카드번호() {
        final PriceVat priceVat = new PriceVat(1000L, 91L);
        final Payment payment = dummyPayment(priceVat);
        final CardInfo cardInfo = payment.getCardInfo();
        payment.setCardInfo(new CardInfo("1234", cardInfo.getExpDate(), "123"));

        assertThrows(ConstraintViolationException.class, () -> paymentService.create(payment));
    }

    @Test
    @DisplayName("결제 생성 _ 잘못된 카드_CVC")
    void when_잘못된_카드정보_cvc() {
        final PriceVat priceVat = new PriceVat(1000L, 91L);
        final Payment payment = dummyPayment(priceVat);
        final CardInfo cardInfo = payment.getCardInfo();
        payment.setCardInfo(new CardInfo(cardInfo.getCardNo(), cardInfo.getExpDate(), String.format("%04d", 9999)));

        assertThrows(ConstraintViolationException.class, () -> paymentService.create(payment));
    }

    private Payment dummyPayment(PriceVat priceVat) {
        CardInfo cardInfo = new CardInfo("1234123412341234", "1226", "123");
        return new Payment(null, cardInfo, 0);
    }
}