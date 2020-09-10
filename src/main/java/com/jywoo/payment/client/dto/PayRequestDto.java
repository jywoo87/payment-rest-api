package com.jywoo.payment.client.dto;

import com.jywoo.payment.client.template.annotations.TemplateField;
import com.jywoo.payment.client.template.annotations.TemplateHeader;
import com.jywoo.payment.client.template.annotations.enums.TemplateFormatterType;
import com.jywoo.payment.domain.Payment;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.domain.enums.TransactionType;
import com.jywoo.payment.domain.validation.annotation.CardExpireDateConstraint;
import com.jywoo.payment.domain.validation.annotation.InstallmentMonthConstraint;
import com.jywoo.payment.domain.validation.annotation.PayClientTransactionIdConstraint;
import com.jywoo.payment.domain.validation.annotation.PriceVatConstraint;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
@PayClientTransactionIdConstraint.List(
        @PayClientTransactionIdConstraint(transactionTypeField = "transactionType", transactionIdField = "payTransactionId", message = "(카드사) 카드사 전송 값에 TRANSACTION ID 필드가 공백입니다.")
)
@PriceVatConstraint.List(
        @PriceVatConstraint(priceFieldName = "price", vatFieldName = "vat", message = "(카드사 전송) 부가가치세가 거래금액보다 큽니다.")
)
public class PayRequestDto {
    @TemplateHeader(type = TemplateFormatterType.DIGIT_R, len = 4)
    private Integer length;

    @TemplateField(formatter = TemplateFormatterType.STRING, len = 10, ordered = 1)
    @NotNull(message = "(카드사 전송) 트랜잭션 타입 값이 없습니다.")
    private TransactionType transactionType;

    @TemplateField(formatter = TemplateFormatterType.STRING, len = 20, ordered = 2)
    @NotNull(message = "(카드사 전송) 관리번호 값이 없습니다.")
    @Size(min = 20, max = 20, message = "(카드사 전송) 관리번호는 {min}~{max}자로 입력되어야 합니다.")
    private String transactionId;

    @TemplateField(formatter = TemplateFormatterType.DIGIT_L, len = 20, ordered = 3)
    @NotNull(message = "(카드사 전송) 카드 번호를 입력해주세요.")
    @Size(min = 10, max = 20, message = "(카드사 전송) 카드 번호는 {min}~{max}자로 입력되어야 합니다.")
    private String cardNo;

    @TemplateField(formatter = TemplateFormatterType.DIGIT_FILL_0, len = 2, ordered = 4)
    @NotNull(message = "(카드사 전송) 할부 개월은 필수 입력 값입니다. (00: 일시불, 02~12 개월)")
    @InstallmentMonthConstraint(message = "(카드사 전송) 할부 개월은 필수 입력 값입니다. (00: 일시불, 02~12 개월)")
    private String installmentMonth;

    @TemplateField(formatter = TemplateFormatterType.DIGIT_L, len = 4, ordered = 5)
    @NotNull(message = "(카드사 전송) 카드 만료일(MMYY)을 입력해주세요.")
    @CardExpireDateConstraint
    private String cardExpMonth;

    @TemplateField(formatter = TemplateFormatterType.DIGIT_L, len = 3, ordered = 6)
    @NotNull(message = "(카드사 전송) 카드 CVC 정보를 확인해주세요.")
    @Size(min = 3, max = 3, message = "(카드사 전송) 카드 CVC 정보를 확인해주세요.")
    private String cardCvc;

    @TemplateField(formatter = TemplateFormatterType.DIGIT_R, len = 10, ordered = 7)
    @NotNull(message = "(카드사 전송) 거래금액(최소 100원)을 확인해주세요.")
    @Min(value = 100, message = "(카드사 전송) 거래금액(최소 100원)을 확인해주세요.")
    private Long price;

    @TemplateField(formatter = TemplateFormatterType.DIGIT_FILL_0, len = 10, ordered = 8)
    @NotNull(message = "(카드사 전송) VAT 값은 필수 값입니다.")
    private Long vat;

    @TemplateField(formatter = TemplateFormatterType.STRING, len = 20, ordered = 9, nullable = true)
    private String payTransactionId;

    @TemplateField(formatter = TemplateFormatterType.STRING, len = 300, ordered = 10)
    @NotNull(message = "(카드사 전송) 암호화된 카드정보는 필수 값입니다.")
    private String encryptedCardInfo;

    @TemplateField(formatter = TemplateFormatterType.STRING, len = 47, ordered = 11, nullable = true)
    private String ext;

    public static PayRequestDto from(Payment payment, Transaction transaction, String encryptedCardInfo) {
        return from(payment, transaction, null, encryptedCardInfo);
    }

    public static PayRequestDto from(Payment payment, Transaction transaction, String payTransactionId, String encryptedCardInfo) {
        return PayRequestDto.builder()
                .transactionType(transaction.getTransactionType())
                .transactionId(transaction.getTransactionId())
                .cardNo(payment.getCardInfo().getCardNo())
                .installmentMonth(String.format("%02d", payment.getInstallmentMonth()))
                .cardExpMonth(payment.getCardInfo().getExpDate())
                .cardCvc(payment.getCardInfo().getCvc())
                .price(transaction.getPriceVat().getPrice())
                .vat(transaction.getPriceVat().getVat())
                .payTransactionId(payTransactionId)
                .encryptedCardInfo(encryptedCardInfo)
                .build();
    }
}
