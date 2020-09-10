package com.jywoo.payment.lock.aop;

import com.jywoo.payment.service.TransactionService;
import com.jywoo.payment.usecase.PaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Disabled
class LockAspectTest {
    private final PaymentUseCase paymentUseCase;
    private final TransactionService transactionService;

    @Test
    @DisplayName("결제 Transaction 생성 AOP 테스트")
    void aopTest() {
        final PaymentUseCase.PaymentRequest paymentRequest =
                new PaymentUseCase.PaymentRequest("1234123412341234", "0623", "123", 0, 11000L, 1000L);
        paymentUseCase.apply(paymentRequest);
    }
}