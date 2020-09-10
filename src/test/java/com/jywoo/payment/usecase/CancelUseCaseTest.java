package com.jywoo.payment.usecase;

import com.jywoo.payment.except.NotEnoughPriceException;
import com.jywoo.payment.except.NotEnoughVatException;
import com.jywoo.payment.except.RemainVatException;
import com.jywoo.payment.except.TransactionNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
class CancelUseCaseTest {
    private final PaymentUseCase paymentUseCase;
    private final CancelUseCase cancelUseCase;

    @Test
    @DisplayName("정상요청")
    void 정상요청() {
        final String transactionId = "20200910203339JSluZM";
        CancelUseCase.CancelResponse cancelResponse = cancelUseCase.apply(new CancelUseCase.CancelRequest(transactionId, 1000L, null));

        assertEquals(cancelResponse.getTransaction().getRemainPriceVat().getPrice(), 10000L);
        assertEquals(cancelResponse.getTransaction().getRemainPriceVat().getVat(), 909L);
    }

    @Test
    @DisplayName("잘못된 관리번호 요청")
    void 잘못된_관리번호_요청() {
        final String transactionId = "20200910203339AAAAAA";
        TransactionNotFoundException e = Assertions.assertThrows(TransactionNotFoundException.class, () -> cancelUseCase.apply(new CancelUseCase.CancelRequest(transactionId, 1000L, null)));
        log.debug(" -- {}", e.getMessage());
    }

    @Test
    @DisplayName("부가가치세가 결제금액보다 큼")
    void 부가가치세_결제금액보다_큰_경우() {
        final String transactionId = "20200910203339JSluZM";
        ConstraintViolationException e = Assertions.assertThrows(ConstraintViolationException.class, () -> cancelUseCase.apply(new CancelUseCase.CancelRequest(transactionId, 100L, 300L)));
        log.debug(" -- {}", e.getMessage());
    }

    @Test
    @DisplayName("값이 입력되지 않았음")
    void 입력되지_않았음() {
        final String transactionId = "20200910203339JSluZM";
        ConstraintViolationException e = Assertions.assertThrows(ConstraintViolationException.class, () -> cancelUseCase.apply(new CancelUseCase.CancelRequest(transactionId, null, null)));
        log.debug(" -- {}", e.getMessage());
    }

    @Test
    @DisplayName("남은 결제금액보다 더 큰 금액을 차감하고자 함")
    void 남은결제금액보다_더큰금액을_차감하고자함() {
        final String transactionId = "20200910203339JSluZM";
        NotEnoughPriceException e = Assertions.assertThrows(NotEnoughPriceException.class, () -> cancelUseCase.apply(new CancelUseCase.CancelRequest(transactionId, 12000L, null)));
        log.debug(" -- {}", e.getMessage());
    }

    @Test
    @DisplayName("남은 부가가치세보다 더 큰 금액을 차감하고자 함")
    void 남은부가가치세보다_더큰금액을_차감하고자함() {
        final String transactionId = "20200910203339JSluZM";
        NotEnoughVatException e = Assertions.assertThrows(NotEnoughVatException.class, () -> cancelUseCase.apply(new CancelUseCase.CancelRequest(transactionId, 11000L, 1001L)));
        log.debug(" -- {}", e.getMessage());
    }

    @Test
    @DisplayName("결제금액은 소진하였으나 부가가치세가 남았음")
    void 결제금액은_모두_소진하였으나_부가가치세_남음() {
        PaymentUseCase.PaymentResponse paymentResponse = paymentUseCase.apply(new PaymentUseCase.PaymentRequest("1234123412341234", "0623", "123", 0, 11000L, null));

        final String transactionId = paymentResponse.getTransaction().getTransactionId();
        RemainVatException e = Assertions.assertThrows(RemainVatException.class, () -> cancelUseCase.apply(new CancelUseCase.CancelRequest(transactionId, 11000L, 500L)));
        log.debug(" -- {}", e.getMessage());
    }

    @Test
    @DisplayName("부가세 입력하지 않고 자동계산에 의해 계산되었으나, 남아있는 부가세가 그보다 작은 경우 에러 없음.")
    void 부가세입력하지않고_요청했으나_남은부가세가_얼마없어_자동소진() {
        PaymentUseCase.PaymentResponse paymentResponse = paymentUseCase.apply(new PaymentUseCase.PaymentRequest("1234123412341234", "0623", "123", 0, 11000L, 300L));

        final String transactionId = paymentResponse.getTransaction().getTransactionId();
        CancelUseCase.CancelResponse response = cancelUseCase.apply(new CancelUseCase.CancelRequest(transactionId, 11000L, null));
        log.debug(" -- {} 원만 차감됨", response.getTransaction().getAutoCalcPriceVat().getVat());
    }

}