package com.jywoo.payment.domain.supported.converter;


import com.jywoo.payment.domain.CardInfo;
import com.jywoo.payment.except.InternalServiceException;
import com.jywoo.payment.util.crypto.AES256CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter
@Component
@RequiredArgsConstructor
@Slf4j
public class CardInfoConverter implements AttributeConverter<CardInfo, String> {
    private final AES256CryptoUtil aes256Util;

    public CardInfoConverter() {
        try {
            aes256Util = new AES256CryptoUtil();
        } catch (UnsupportedEncodingException e) {
            throw new InternalServiceException();
        }
    }

    @Override
    public String convertToDatabaseColumn(CardInfo attribute) {
        try {
            final String combinedCardInfo = Stream.of(attribute.getCardNo(), attribute.getExpDate(), attribute.getCvc())
                    .map(String::valueOf)
                    .collect(Collectors.joining("|"));
            return aes256Util.encrypt(combinedCardInfo);
        } catch (NoSuchAlgorithmException e) {
            log.error(" -- cardInfo encrypt error", e);
        } catch (GeneralSecurityException e) {
            log.error(" -- cardInfo encrypt error", e);
        } catch (UnsupportedEncodingException e) {
            log.error(" -- cardInfo encrypt error", e);
        }

        return null;
    }

    @Override
    public CardInfo convertToEntityAttribute(String dbData) {
        try {
            final String decryptedCardInfo = aes256Util.decrypt(dbData);
            final String[] splitDecryptedCardInfo =  decryptedCardInfo.split("\\|");

            return CardInfo.builder()
                    .cardNo(splitDecryptedCardInfo[0])
                    .expDate(splitDecryptedCardInfo[1])
                    .cvc(splitDecryptedCardInfo[2])
                    .build();
        } catch (NoSuchAlgorithmException e) {
            log.error(" -- cardInfo decrypt error", e);
        } catch (GeneralSecurityException e) {
            log.error(" -- cardInfo decrypt error", e);
        } catch (UnsupportedEncodingException e) {
            log.error(" -- cardInfo decrypt error", e);
        } catch (IndexOutOfBoundsException e) {
            log.error(" -- cardInfo decrypt error", e);
        }

        return null;
    }
}
