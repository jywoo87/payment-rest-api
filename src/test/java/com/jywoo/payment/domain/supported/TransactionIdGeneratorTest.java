package com.jywoo.payment.domain.supported;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
@Disabled
class TransactionIdGeneratorTest {

    @Autowired
    private TransactionIdGenerator transactionIdGenerator;

    @Test
    @DisplayName("아이디 생성 테스트")
    void testGeneratedTid() {
        String generatedId = transactionIdGenerator.generate(LocalDateTime.parse("2020-09-09T19:05:02"));

        log.debug(" -- generated Transaction Id : {}", generatedId);

        assertTrue(generatedId.startsWith("20200909190502"));
        assertEquals(generatedId.length(), 20);
    }
}