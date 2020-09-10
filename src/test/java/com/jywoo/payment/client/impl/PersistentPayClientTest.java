package com.jywoo.payment.client.impl;

import com.jywoo.payment.client.dto.PayRequestDto;
import com.jywoo.payment.domain.CardInfo;
import com.jywoo.payment.domain.enums.TransactionType;
import com.jywoo.payment.domain.supported.converter.CardInfoConverter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Disabled
class PersistentPayClientTest {
    private final PersistentPayClient payClient;
    private final CardInfoConverter cardInfoConverter;

    private final String RESULT_SUCCESS = "_446PAYMENT___20200909153009ABCDEF1234123456785678____000623123______10000000000091____________________94gtmAWwAFC8zyX1VApHweL6smlfVLVoS5sNUpwcHUQ=_______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________";

    @Test
    @DisplayName("카드사 요청 - 정상")
    void send() {
        PayRequestDto payRequestDto = getPayRequestDto();
        String result = payClient.send(payRequestDto);

        assertEquals(result, RESULT_SUCCESS);
    }

    @Test
    @DisplayName("카드사 요청 - 요청 타입 취소시 관리번호 누락")
    void when_요청타입_관리번호누락() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setTransactionType(TransactionType.CANCEL);
        payRequestDto.setPayTransactionId(null);

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 요청 타입, 관리번호 둘 다 누락")
    void when_요청타입_관리번호_모두_누락() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setTransactionType(null);
        payRequestDto.setPayTransactionId(null);

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 결제금액보다 부가세가 같거나 큰 경우")
    void when_결제금액보다_부가세가_같거나_큰_경우() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setPrice(100L);
        payRequestDto.setVat(100L);

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 요청 타입 없음")
    void when_요청_타입_없음() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setTransactionType(null);

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 관리번호 포맷 확인")
    void when_관리번호_포맷확인() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setTransactionId("12345678");

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 카드번호 포맷확인")
    void when_카드번호_포맷확인() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setCardNo("123445");

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 할부기간 널 체크")
    void when_할부기간_널체크() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setInstallmentMonth(null);

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 카드유효기간 포맷체크")
    void when_카드유효기간_포맷체크() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setCardExpMonth("0820"); //지난날짜

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - cvc 체크")
    void when_카드_CVC_체크() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setCardCvc("11"); //100이하

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 거래금액 100원 미만")
    void when_거래금액_확인() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setPrice(99L);

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - VAT가 널인 경우")
    void when_VAT_널체크() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setVat(null);

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }

    @Test
    @DisplayName("카드사 요청 - 암호화 카드정보 필드 널인 경우")
    void when_encrypted_card_info_널() {
        PayRequestDto payRequestDto = getPayRequestDto();
        payRequestDto.setEncryptedCardInfo(null);

        Assertions.assertThrows(ConstraintViolationException.class, () -> payClient.send(payRequestDto));
    }



    PayRequestDto getPayRequestDto() {
        CardInfo cardInfo = CardInfo.builder()
                .cardNo("1234123456785678")
                .expDate("0623")
                .cvc("123")
                .build();

        return PayRequestDto.builder()
                .cardExpMonth(cardInfo.getExpDate())
                .cardNo(cardInfo.getCardNo())
                .cardCvc(cardInfo.getCvc())
                .price(1000L)
                .vat(91L)
                .installmentMonth("00")
                .transactionId("20200909153009ABCDEF")
                .transactionType(TransactionType.PAYMENT)
                .encryptedCardInfo(cardInfoConverter.convertToDatabaseColumn(cardInfo))
                .build();
    }

}