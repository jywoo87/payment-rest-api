package com.jywoo.payment.domain.validation;

import com.jywoo.payment.domain.enums.TransactionType;
import com.jywoo.payment.domain.validation.annotation.PayClientTransactionIdConstraint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class PayClientTransactionIdValidator implements ConstraintValidator<PayClientTransactionIdConstraint, Object> {

    private String typeField;
    private String idField;

    @Override
    public void initialize(PayClientTransactionIdConstraint constraintAnnotation) {
        typeField = constraintAnnotation.transactionTypeField();
        idField = constraintAnnotation.transactionIdField();

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            final String type = BeanUtils.getProperty(value, typeField);
            final String id = BeanUtils.getProperty(value, idField);

            if(type == null && id == null) {
                return true;
            }

            TransactionType transactionType = TransactionType.valueOf(type);
            if(transactionType == TransactionType.CANCEL) {
                return !StringUtils.isEmpty(id);
            }

            return true;
        } catch (IllegalAccessException e) {
            log.error("-- PayClientTransactionIdValidator exception : ", e);
        } catch (InvocationTargetException e) {
            log.error("-- PayClientTransactionIdValidator exception : ", e);
        } catch (NoSuchMethodException e) {
            log.error("-- PayClientTransactionIdValidator exception : ", e);
        }
        return false;
    }
}
