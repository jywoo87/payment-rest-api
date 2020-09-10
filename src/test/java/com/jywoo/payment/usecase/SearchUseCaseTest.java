package com.jywoo.payment.usecase;

import com.jywoo.payment.domain.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
class SearchUseCaseTest {
    private final PaymentUseCase paymentUseCase;
    private final SearchUseCase searchUseCase;

    @Test
    @DisplayName("관리번호 올바르게 입력되지 않았음 1")
    void 관리번호_올바르지_않음_1() {
        ConstraintViolationException e1 = Assertions.assertThrows(ConstraintViolationException.class, () -> searchUseCase.apply(new SearchUseCase.SearchRequest("")));
        log.debug("-- {}", e1.getMessage());

        ConstraintViolationException e2 = Assertions.assertThrows(ConstraintViolationException.class, () -> searchUseCase.apply(new SearchUseCase.SearchRequest(null)));
        log.debug("-- {}", e2.getMessage());
    }

    @Test
    @DisplayName("정상호출")
    void 정상호출() {
        PaymentUseCase.PaymentResponse paymentResponse = paymentUseCase.apply(new PaymentUseCase.PaymentRequest("1234123412341234", "0623", "123", 0, 11000L, 300L));

        SearchUseCase.SearchResponse searchResponse = searchUseCase.apply(new SearchUseCase.SearchRequest(paymentResponse.getTransaction().getTransactionId()));
        log.debug("{}", searchResponse.getTransaction());
        log.debug("{}", searchResponse.getTransaction().getTransactionId());
        Assertions.assertEquals(searchResponse.getTransaction().getTransactionId(), paymentResponse.getTransaction().getTransactionId());
        Assertions.assertEquals(searchResponse.getTransaction().getTransactionType(), TransactionType.PAYMENT);
    }


}