package com.jywoo.payment.domain.validation;

import com.jywoo.payment.domain.validation.annotation.InstallmentMonthConstraint;
import com.jywoo.payment.except.InternalServiceException;
import com.jywoo.payment.except.message.ExceptionMessage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InstallmentMonthValidator implements ConstraintValidator<InstallmentMonthConstraint, Object> {

    @Override
    public void initialize(InstallmentMonthConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Integer value = null;
        if (object instanceof Integer) {
            value = (Integer) object;
        } else if (object instanceof String) {
            value = Integer.parseInt(String.valueOf(object));
        } else {
            new InternalServiceException(ExceptionMessage.INTERNAL_NOT_INITIALIZED_METHOD);
        }

        if(value == null) {
            return true;
        }
        if(value == 1) {
            return false;
        }

        return value > -1 && value < 13;
    }
}
