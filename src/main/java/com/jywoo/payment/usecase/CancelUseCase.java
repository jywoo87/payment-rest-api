package com.jywoo.payment.usecase;

import com.jywoo.payment.client.dto.PayRequestDto;
import com.jywoo.payment.client.impl.PersistentPayClient;
import com.jywoo.payment.domain.Payment;
import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.domain.enums.Operation;
import com.jywoo.payment.domain.supported.converter.CardInfoConverter;
import com.jywoo.payment.except.message.ExceptionMessage;
import com.jywoo.payment.except.message.ValidationExceptionMessage;
import com.jywoo.payment.lock.annotations.OperationLock;
import com.jywoo.payment.service.TransactionService;
import com.jywoo.payment.usecase.intr.Request;
import com.jywoo.payment.usecase.intr.Response;
import com.jywoo.payment.usecase.intr.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelUseCase implements UseCase<CancelUseCase.CancelRequest, CancelUseCase.CancelResponse> {
    private final TransactionService transactionService;
    private final CardInfoConverter cardInfoConverter;
    private final PersistentPayClient payClient;

    @Override
    @Transactional
    @OperationLock(operation = Operation.PAYMENT, expression = "#cancelRequest.transactionId", exceptionMessage = ExceptionMessage.CONCURRENCY_EXCEPTION_TRANSACTION)
    public CancelUseCase.CancelResponse apply(CancelRequest cancelRequest) {
        final PriceVat priceVat = PriceVat.from(cancelRequest);
        final Transaction transaction = transactionService.createCancelTransaction(cancelRequest.getTransactionId(), priceVat);
        final Payment payment = transaction.getPayment();
        final String encryptedCardInfo = cardInfoConverter.convertToDatabaseColumn(payment.getCardInfo());

        final String cancelRequestString = payClient.send(PayRequestDto.from(payment, transaction, cancelRequest.getTransactionId(), encryptedCardInfo));
        return new CancelResponse(cancelRequest, transaction, cancelRequestString);
    }

    @Getter
    @AllArgsConstructor
    public static class CancelRequest extends AbstractPriceVatRequest implements Request {
        @NotNull(message = ValidationExceptionMessage.REQUIRED_TRANSACTION_ID)
        @Size(min = 20, max = 20, message = ValidationExceptionMessage.INVALID_TRANSACTION_ID)
        private String transactionId;

        @NotNull(message = ValidationExceptionMessage.REQUIRED_PAYMENT_PRICE)
        @Min(value = 10, message = ValidationExceptionMessage.INVALID_PAY_PRICE_MIN)
        @Max(value = 1000000000, message = ValidationExceptionMessage.INVALID_PAY_PRICE_MAX)
        private Long price; // 결제금액

        private Long vat; //vat
    }

    @Getter
    @AllArgsConstructor
    public static class CancelResponse implements Response {
        private final CancelRequest request;
        private final Transaction transaction;
        private final String payRequestString;
    }

}
