package com.jywoo.payment.domain.validation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CardExpireDateValidatorBaseTest {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMyy");

    @Test
    @DisplayName("카드만료일 잘못된 날짜 포맷")
    void when_잘못된_날짜_포맷() {
        Assertions.assertThrows(DateTimeException.class, () -> {
            YearMonth yearMonth = YearMonth.from(dateTimeFormatter.parse("2013"));
            log.debug("-- yearMonth : {}", yearMonth);
        });
    }

    @Test
    @DisplayName("카드만료일 연월비교")
    void when_연월비교() {
        YearMonth currentYearMonth = YearMonth.of(2020, 9);
        YearMonth yearMonth = YearMonth.from(dateTimeFormatter.parse("0920"));
        log.debug("-- currentYearMonth: {}, yearMonth : {}, compare : {}", currentYearMonth, yearMonth, currentYearMonth.compareTo(yearMonth));

        yearMonth = YearMonth.from(dateTimeFormatter.parse("0820"));
        log.debug("-- currentYearMonth: {}, yearMonth : {}, compare : {}", currentYearMonth, yearMonth, currentYearMonth.compareTo(yearMonth));

        yearMonth = YearMonth.from(dateTimeFormatter.parse("1020"));
        log.debug("-- currentYearMonth: {}, yearMonth : {}, compare : {}", currentYearMonth, yearMonth, currentYearMonth.compareTo(yearMonth));

    }



}