package com.jywoo.payment.service;

import com.jywoo.payment.domain.CardInfo;
import com.jywoo.payment.domain.Payment;
import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.domain.enums.TransactionType;
import com.jywoo.payment.except.BadRequestException;
import com.jywoo.payment.except.RemainVatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Disabled
class TransactionServiceTest {
    private final PaymentService paymentService;
    private final TransactionService transactionService;

    @Test
    @DisplayName("결제 Transaction 생성")
    void create_Payment_트랜잭션() {
        final PriceVat priceVat = dummyPriceVat();
        final Payment payment = dummyPayment(priceVat);
        dummyCreate(payment, priceVat);

        //PAYMENT 타입의 트랜잭션 다시 입력 -- PAYMENT 타입의 트랜잭션은 하나만 존재할 수 있다.
        assertThrows(BadRequestException.class, () -> {
            transactionService.createPaymentTransaction(payment, priceVat);
        });
    }

    @Test
    @DisplayName("취소 Transaction 생성")
    void create_Cancel_트랜잭션() {
        final PriceVat priceVat = dummyPriceVat();
        final Payment payment = dummyPayment(priceVat);
        final Transaction savedTransaction = dummyCreate(payment, priceVat);

        final PriceVat cancelPriceVat = new PriceVat(500L, null);

        // 1
        transactionService.createCancelTransaction(savedTransaction.getTransactionId(), cancelPriceVat);

        // 2
        assertThrows(RemainVatException.class, () -> transactionService.createCancelTransaction(savedTransaction.getTransactionId(), cancelPriceVat));

        //3
        final PriceVat cancelPriceVat2 = new PriceVat(500L, 46L);
        transactionService.createCancelTransaction(savedTransaction.getTransactionId(), cancelPriceVat2);
    }

    private Transaction dummyCreate(Payment payment, PriceVat priceVat) {
        paymentService.create(payment);

        final Transaction transaction = dummyTransaction(payment, TransactionType.PAYMENT, priceVat);
        final Transaction savedTransaction = transactionService.createPaymentTransaction(payment, priceVat);
        log.debug("{}", savedTransaction);

        assertNotNull(savedTransaction);
        assertNotNull(savedTransaction.getTransactionId());

        assertEquals(savedTransaction.getTransactionType(), transaction.getTransactionType());
        assertEquals(savedTransaction.getPayment().getPaymentId(), payment.getPaymentId());
        assertEquals(savedTransaction.getPriceVat().getPrice(), priceVat.getPrice());
        assertEquals(savedTransaction.getPriceVat().getVat(), priceVat.getVat());

        return savedTransaction;
    }

    @Test
    @DisplayName("결제 Transaction 생성 _ 잘못된 부가세")
    void create_Payment_트랜잭션_잘못된_부가세() {
        final PriceVat priceVat = new PriceVat(1000L, 1200L);
        final Payment payment = dummyPayment(priceVat);

        assertThrows(TransactionSystemException.class, () -> {
            paymentService.create(payment);
            transactionService.createPaymentTransaction(payment, priceVat);
        });
    }


    private PriceVat dummyPriceVat() {
        return new PriceVat(1000L, 91L);
    }

    private Payment dummyPayment(PriceVat priceVat) {
        CardInfo cardInfo = new CardInfo("1234123412341234", "1226", "123");
        return new Payment(null, cardInfo, 0);
    }

    private Transaction dummyTransaction(Payment payment, TransactionType transactionType, PriceVat priceVat) {
        return Transaction.build(transactionType, payment, priceVat);
    }
}