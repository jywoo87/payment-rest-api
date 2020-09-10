package com.jywoo.payment.usecase;

import com.jywoo.payment.client.dto.PayRequestDto;
import com.jywoo.payment.client.impl.PersistentPayClient;
import com.jywoo.payment.domain.Payment;
import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.domain.enums.Operation;
import com.jywoo.payment.domain.supported.converter.CardInfoConverter;
import com.jywoo.payment.domain.validation.annotation.CardExpireDateConstraint;
import com.jywoo.payment.domain.validation.annotation.InstallmentMonthConstraint;
import com.jywoo.payment.domain.validation.annotation.PriceVatConstraint;
import com.jywoo.payment.except.message.ExceptionMessage;
import com.jywoo.payment.except.message.ValidationExceptionMessage;
import com.jywoo.payment.lock.annotations.OperationLock;
import com.jywoo.payment.service.PaymentService;
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
import javax.validation.constraints.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentUseCase implements UseCase<PaymentUseCase.PaymentRequest, PaymentUseCase.PaymentResponse> {
    private final PaymentService paymentService;
    private final TransactionService transactionService;
    private final PersistentPayClient payClient;
    private final CardInfoConverter cardInfoConverter;

    @Override
    @Transactional
    @OperationLock(operation = Operation.PAYMENT, expression = "#paymentRequest.cardNo", exceptionMessage = ExceptionMessage.CONCURRENCY_EXCEPTION_CARD_NO )
    public PaymentUseCase.PaymentResponse apply(PaymentRequest paymentRequest) {
        final Payment payment = paymentService.create(Payment.from(paymentRequest));
        final Transaction transaction = transactionService.createPaymentTransaction(payment, PriceVat.from(paymentRequest));
        final String encryptedCardInfo = cardInfoConverter.convertToDatabaseColumn(payment.getCardInfo());
        final String requestStr = payClient.send(PayRequestDto.from(payment, transaction, encryptedCardInfo));

        return new PaymentResponse(paymentRequest, transaction, requestStr);
    }


    @Getter
    @AllArgsConstructor
    @PriceVatConstraint.List({
            @PriceVatConstraint(priceFieldName = "price", vatFieldName = "vat")
    })
    public static class PaymentRequest extends AbstractPriceVatRequest implements Request {
        @NotNull(message = ValidationExceptionMessage.REQUIRED_CARD_NO)
        @Pattern(regexp = "[0-9]{10,16}", message = ValidationExceptionMessage.INVALID_CARD_NO)
        private String cardNo; // 카드번호

        @NotNull(message = ValidationExceptionMessage.REQUIRED_CARD_EXP_DATE)
        @CardExpireDateConstraint
        private String expDate; // 만료일

        @NotNull(message = ValidationExceptionMessage.REQUIRED_CARD_CVC)
        @Pattern(regexp = "[0-9]{3}", message = ValidationExceptionMessage.INVALID_CARD_CVC)
        private String cvc; // cvc

        @NotNull(message = ValidationExceptionMessage.REQUIRED_INSTALLMENT_MONTH)
        @InstallmentMonthConstraint
        private Integer installmentMonth; //할부개월수

        public Integer getInstallmentMonth() {
            if(this.installmentMonth == null) {
                return 0;
            }
            return this.installmentMonth;
        }

        @NotNull(message = ValidationExceptionMessage.REQUIRED_PAYMENT_PRICE)
        @Min(value = 10, message = ValidationExceptionMessage.INVALID_PAY_PRICE_MIN)
        @Max(value = 1000000000, message = ValidationExceptionMessage.INVALID_PAY_PRICE_MAX)
        private Long price; // 결제금액

        private Long vat; //vat
    }

    @Getter
    @AllArgsConstructor
    public static class PaymentResponse implements Response {
        private final PaymentRequest paymentRequest;
        private final Transaction transaction;
        private final String payRequestString;
    }

}
