package com.jywoo.payment.domain.supported;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@Slf4j
public class TransactionIdGenerator implements IdentifierGenerator {
    private Random random = new Random();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private int CHARS_LEN = CHARS.length();

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generate();
    }

    public String generate() {
        return generate(LocalDateTime.now());
    }

    public String generate(LocalDateTime localDateTime) {
        final String base = dateTimeFormatter.format(localDateTime);
        final String transactionId = base + generateRandomString(6);

        return transactionId;
    }

    private String generateRandomString(int len) {
        return IntStream.range(0, len)
                .mapToObj(i -> randomChar())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private char randomChar() {
        return CHARS.charAt(random.nextInt(CHARS_LEN));
    }
}
