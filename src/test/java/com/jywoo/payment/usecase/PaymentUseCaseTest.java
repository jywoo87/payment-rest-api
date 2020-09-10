package com.jywoo.payment.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
class PaymentUseCaseTest {
    private final PaymentUseCase paymentUseCase;

    @Test
    @DisplayName("정상요청 부가세 입력안함")
    void 정상요청_부가세_입력안함() {
        PaymentUseCase.PaymentResponse paymentResponse = paymentUseCase.apply(new PaymentUseCase.PaymentRequest("1234123412341234", "0623", "123", 0, 1000L, null));
        log.debug("{}", paymentResponse.getTransaction());
        log.debug("{}", paymentResponse.getTransaction().getTransactionId() );

        Assertions.assertEquals(paymentResponse.getTransaction().getPriceVat().getPrice(), 1000L);
        Assertions.assertEquals(paymentResponse.getTransaction().getPriceVat().getVat(), 91L);
    }

    @Test
    @DisplayName("정상요청 부가세 입력함")
    void 정상요청_부가세_입력함() {
        PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("1234123412341234", "0623", "123", 0, 11000L, 300L);
        PaymentUseCase.PaymentResponse paymentResponse = paymentUseCase.apply(paymentRequest);
        log.debug("{}", paymentResponse.getTransaction());
        log.debug("{}", paymentResponse.getTransaction().getTransactionId() );

        Assertions.assertEquals(paymentResponse.getTransaction().getPriceVat().getPrice(), 11000L);
        Assertions.assertEquals(paymentResponse.getTransaction().getPriceVat().getVat(), 300L);
    }

    @Test
    @DisplayName("카드번호 잘못입력")
    void 카드번호_잘못입력() {
        PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("1234", "0623", "123", 0, 11000L, 300L);

        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> paymentUseCase.apply(paymentRequest));
        log.debug("-- {}", e.getMessage());
    }

    @Test
    @DisplayName("카드 만료일 잘못입력 (지난날짜)")
    void 카드_만료일_잘못입력_지난날짜() {
        PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("123412341234", "0620", "123", 0, 11000L, 300L);

        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> paymentUseCase.apply(paymentRequest));
        log.debug("-- {}", e.getMessage());
    }

    @Test
    @DisplayName("카드 만료일 잘못입력 (포맷비정상)")
    void 카드_만료일_잘못입력_포맷비정상() {
        PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("123412341234", "6621", "123", 0, 11000L, 300L);

        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> paymentUseCase.apply(paymentRequest));
        log.debug("-- {}", e.getMessage());
    }

    @Test
    @DisplayName("카드 cvc 잘못입력")
    void cvc_잘못입력() {
        PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("123412341234", "1225", "12", 0, 11000L, 300L);

        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> paymentUseCase.apply(paymentRequest));
        log.debug("-- {}", e.getMessage());
    }

    @Test
    @DisplayName("할부날짜 잘못입력")
    void 할부날짜_잘못입력() {
        PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("123412341234", "1225", "123", 1, 11000L, 300L);

        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> paymentUseCase.apply(paymentRequest));
        log.debug("-- {}", e.getMessage());
    }

    @Test
    @DisplayName("결제금액이 최소금액보다 작음")
    void 결제금액_최소금액보다_작음() {
        PaymentUseCase.PaymentRequest paymentRequest = new PaymentUseCase.PaymentRequest("123412341234", "1225", "123", 0, 7L, null);

        ConstraintViolationException e = assertThrows(ConstraintViolationException.class, () -> paymentUseCase.apply(paymentRequest));
        log.debug("-- {}", e.getMessage());
    }
}