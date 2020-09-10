package com.jywoo.payment.domain.validation;

import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.validation.annotation.PriceVatConstraint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class PriceVatValidator implements ConstraintValidator<PriceVatConstraint, Object> {
    private String priceFieldName;
    private String vatFieldName;

    @Override
    public void initialize(PriceVatConstraint constraintAnnotation) {
        priceFieldName = constraintAnnotation.priceFieldName();
        vatFieldName = constraintAnnotation.vatFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value instanceof PriceVat) {
            PriceVat priceVat = (PriceVat) value;
            log.debug("price : {}, vat : {}", priceVat.getPrice(), priceVat.getVat());
            return priceVat.getVat() <= priceVat.getPrice();
        }

        if(StringUtils.isEmpty(priceFieldName) || StringUtils.isEmpty(vatFieldName)) {
            log.error("-- PriceVatValidator priceFieldName or vatFieldName is null : {}", value);
            return false;
        }

        try {
            String price = BeanUtils.getProperty(value, priceFieldName);
            String vat = BeanUtils.getProperty(value, vatFieldName);

            log.debug("price : {}, vat : {}", price, vat);
            if(price == null || vat == null) {
                return true;
            }

            return Long.valueOf(price) > Long.valueOf(vat);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return false;
    }
}
