package com.jywoo.payment;

import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.domain.enums.TransactionType;
import com.jywoo.payment.except.NotEnoughPriceException;
import com.jywoo.payment.except.NotEnoughVatException;
import com.jywoo.payment.except.RemainVatException;
import com.jywoo.payment.usecase.CancelUseCase;
import com.jywoo.payment.usecase.PaymentUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
class CancelTest {
    private final PaymentUseCase paymentUseCase;
    private final CancelUseCase cancelUseCase;

    private final String cardNo = "1234123412341234";
    private final String cardExpDate = "0724";
    private final String cvc = "012";
    private final Integer installmentMonth = 0;

    @Test
    @DisplayName("부분 취소 TestCase1")
    void 부분취소_TestCase1() {
        List<Object> result = new ArrayList<>();

        final PaymentUseCase.PaymentResponse paymentResponse = paymentUseCase.apply(
                new PaymentUseCase.PaymentRequest(cardNo, cardExpDate, cvc, installmentMonth, 11000L, 1000L)
        );
        result.add(paymentResponse);

        final String transactionId = paymentResponse.getTransaction().getTransactionId();

        result.add(cancelUseCase.apply(getCancelRequest(transactionId, 1100L, 100L)));
        result.add(cancelUseCase.apply(getCancelRequest(transactionId, 3300L, null)));
        result.add(assertThrows(NotEnoughPriceException.class, () -> cancelUseCase.apply(getCancelRequest(transactionId, 7000L, null))));
        result.add(assertThrows(NotEnoughVatException.class, () -> cancelUseCase.apply(getCancelRequest(transactionId, 6600L, 700L))));
        result.add(cancelUseCase.apply(getCancelRequest(transactionId, 6600L, 600L)));
        result.add(assertThrows(NotEnoughPriceException.class, () -> cancelUseCase.apply(getCancelRequest(transactionId, 100L, null))));

        showResult(result);
    }

    @Test
    @DisplayName("부분 취소 TestCase2")
    void 부분취소_TestCase2() {
        List<Object> result = new ArrayList<>();

        final PaymentUseCase.PaymentResponse paymentResponse = paymentUseCase.apply(new PaymentUseCase.PaymentRequest(cardNo, cardExpDate, cvc, installmentMonth, 20000L, 909L));
        final String transactionId = paymentResponse.getTransaction().getTransactionId();

        result.add(paymentResponse);
        result.add(cancelUseCase.apply(getCancelRequest(transactionId, 10000L, 0L)));
        result.add(assertThrows(RemainVatException.class, () -> cancelUseCase.apply(getCancelRequest(transactionId, 10000L, 0L))));
        result.add(cancelUseCase.apply(getCancelRequest(transactionId, 10000L, 909L)));

        showResult(result);
    }

    @Test
    @DisplayName("부분 취소 TestCase3")
    void 부분취소_TestCase3() {
        List<Object> result = new ArrayList<>();

        final PaymentUseCase.PaymentResponse paymentResponse = paymentUseCase.apply(new PaymentUseCase.PaymentRequest(cardNo, cardExpDate, cvc, installmentMonth, 20000L, null));
        final String transactionId = paymentResponse.getTransaction().getTransactionId();

        result.add(paymentResponse);
        result.add(cancelUseCase.apply(getCancelRequest(transactionId, 10000L, 1000L)));
        result.add(assertThrows(NotEnoughVatException.class, () -> cancelUseCase.apply(getCancelRequest(transactionId, 10000L, 909L))));
        result.add(cancelUseCase.apply(getCancelRequest(transactionId, 10000L, null)));

        showResult(result);
    }

    CancelUseCase.CancelRequest getCancelRequest(String transactionId, Long price, Long vat) {
        return new CancelUseCase.CancelRequest(transactionId, price, vat);
    }

    public void showResult(List<Object> objects) {
        printHeader("구분", "결제/취소금액", "부가가치세", "결과", "결제상태인 금액", "결제상태인 부가가치세", "설명");
        objects.stream()
                .map(object -> get(object))
                .filter(obj -> obj != null)
                .forEach(t -> print(t.getException() == null, t));
    }

    @Builder
    @Getter
    static class PrintModel {
        private Transaction transaction;
        private TransactionType transactionType;
        private PriceVat remainPriceVat;
        private PriceVat requestPriceVat;
        private Exception exception;
    };

    private PrintModel get(Object response) {
        if (response instanceof PaymentUseCase.PaymentResponse) {
            PaymentUseCase.PaymentResponse res = (PaymentUseCase.PaymentResponse) response;
            PaymentUseCase.PaymentRequest paymentRequest = res.getPaymentRequest();
            Transaction transaction = res.getTransaction();

            return PrintModel.builder()
                    .transaction(res.getTransaction())
                    .transactionType(transaction.getTransactionType())
                    .requestPriceVat(new PriceVat(paymentRequest.getPrice(), paymentRequest.getVat()))
                    .remainPriceVat(transaction.getRemainPriceVat())
                    .build();
        } else if (response instanceof CancelUseCase.CancelResponse) {
            CancelUseCase.CancelResponse res = (CancelUseCase.CancelResponse) response;
            CancelUseCase.CancelRequest cancelRequest = res.getRequest();
            Transaction transaction = res.getTransaction();

            return PrintModel.builder()
                    .transaction(res.getTransaction())
                    .transactionType(transaction.getTransactionType())
                    .requestPriceVat(new PriceVat(cancelRequest.getPrice(), cancelRequest.getVat()))
                    .remainPriceVat(transaction.getRemainPriceVat())
                    .build();
        } else if (response instanceof NotEnoughPriceException) {
            NotEnoughPriceException res = (NotEnoughPriceException) response;
            Transaction transaction = res.getTransaction();

            return PrintModel.builder()
                    .transaction(res.getTransaction())
                    .transactionType(transaction.getTransactionType())
                    .requestPriceVat(res.getPriceVat())
                    .remainPriceVat(res.getRemainPriceVat())
                    .exception(res)
                    .build();
        } else if (response instanceof NotEnoughVatException) {
            NotEnoughVatException res = (NotEnoughVatException) response;
            Transaction transaction = res.getTransaction();

            return PrintModel.builder()
                    .transaction(res.getTransaction())
                    .transactionType(transaction.getTransactionType())
                    .requestPriceVat(res.getPriceVat())
                    .remainPriceVat(res.getRemainPriceVat())
                    .exception(res)
                    .build();
        } else if (response instanceof RemainVatException) {
            RemainVatException res = (RemainVatException) response;
            Transaction transaction = res.getTransaction();

            return PrintModel.builder()
                    .transaction(res.getTransaction())
                    .transactionType(transaction.getTransactionType())
                    .requestPriceVat(res.getPriceVat())
                    .remainPriceVat(res.getRemainPriceVat())
                    .exception(res)
                    .build();
        }
        log.debug("-- not found handler : {}", response.getClass());
        return null;
    }

    private void print(boolean success, PrintModel printModel) {
        printRow(printModel.getTransactionType().getDescription(),
                printModel.getRequestPriceVat().getPrice(),
                printModel.getRequestPriceVat().getOriginVat(),
                success ? "성공" : "실패",
                printModel.getRemainPriceVat().getPrice(),
                printModel.getRemainPriceVat().getVat(),
                success ?
                        buildDescription(printModel.getTransaction(), printModel.getRequestPriceVat()) :
                        printModel.getException().getMessage()
        );
    }

    private void printHeader(Object ... object) {
        log.info("{}", String.format("%-10s\t%10s\t\t%10s\t\t%10s\t\t%10s\t\t%10s\t\t%-100s", object));
    }

    private void printRow(Object ... object) {
        log.info("{}", String.format("%-10s\t%,10d\t\t%,10d\t\t%10s\t\t%,10d\t\t%,10d\t\t%-100s", object));
    }

    private String buildDescription(Transaction transaction,  PriceVat priceVat) {
        if (transaction.getTransactionType() == TransactionType.PAYMENT) {
            return String.format("%,d(%,d)원 결제 성공",
                    priceVat.getPrice(),
                    priceVat.getVat());

        }

        if(transaction.getPriceVat().isAutoCalcVat() && transaction.getAutoCalcPriceVat().getVat() != transaction.getPriceVat().getVat()) {
            return String.format("%,d원 취소, 남은 부가가치세는 %,d 원으로 자동 계산되어 성공",
                    priceVat.getPrice(),
                    priceVat.getVat(),
                    transaction.getAutoCalcPriceVat().getVat());
        }
        return String.format("%,d(%,d)원 결제 취소",
                priceVat.getPrice(),
                priceVat.getVat());
    }
}
