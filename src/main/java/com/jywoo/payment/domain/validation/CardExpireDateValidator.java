package com.jywoo.payment.domain.validation;

import com.jywoo.payment.domain.validation.annotation.CardExpireDateConstraint;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CardExpireDateValidator implements ConstraintValidator<CardExpireDateConstraint, String> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMyy");

    @Override
    public void initialize(CardExpireDateConstraint constraint) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if(value == null) {
                return true;
            }
            final YearMonth yearMonth = YearMonth.from(dateTimeFormatter.parse(value));
            final YearMonth currentYearMonth = YearMonth.now();

            return currentYearMonth.compareTo(yearMonth) < 1;
        } catch (DateTimeException e) {
            log.error("-- invalid cardExpireDate : {}, {}", value, e);
        }

        return false;
    }

}