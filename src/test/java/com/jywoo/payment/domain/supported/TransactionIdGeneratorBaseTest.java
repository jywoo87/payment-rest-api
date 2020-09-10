package com.jywoo.payment.domain.supported;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Disabled
class TransactionIdGeneratorBaseTest {
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private int CHARS_LEN = CHARS.length();
    private Random random = new Random(System.currentTimeMillis());

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(ints = {6, 4, 5, 3})
    @DisplayName("랜덤 스트링 생성 테스트")
    void testRandomString(int len) {
        String str = generateRandomString(len);
        log.debug("-- randomString [{}] : {}", len, str);

        assertEquals(str.length(), len);
    }

    @Test
    @DisplayName("아이디 생성 테스트")
    void testGen() {
        String base = dateTimeFormatter.format(LocalDateTime.parse("2020-09-09T19:05:02"));
        String generatedId = base + generateRandomString(6);
        log.debug(" -- generatedId : {}", generatedId);

        assertTrue(generatedId.startsWith("20200909190502"));
        assertEquals(generatedId.length(), 20);
    }

    String generateRandomString(int len) {
        return IntStream.range(0, len)
                .mapToObj(i -> randomChar())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    char randomChar() {
        return CHARS.charAt(random.nextInt(CHARS_LEN));
    }
}