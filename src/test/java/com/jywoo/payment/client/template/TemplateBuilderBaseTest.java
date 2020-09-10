package com.jywoo.payment.client.template;

import com.jywoo.payment.client.dto.PayRequestDto;
import com.jywoo.payment.client.template.annotations.TemplateField;
import com.jywoo.payment.client.template.cache.TemplateFieldMeta;
import com.jywoo.payment.domain.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Disabled
class TemplateBuilderBaseTest {
    private final Map<Class<?>, List<TemplateFieldMeta>> cached = new HashMap<>();

    @Test
    @DisplayName("Template 캐시 테스트")
    void testCached() {
        final PayRequestDto dto = dummy();
        final Class<?> clazz = dto.getClass();

        List<TemplateFieldMeta> templateFieldItems = getTemplate(clazz);
        log.debug("templateFieldItems - {}", templateFieldItems);

        templateFieldItems = getTemplate(clazz);
        log.debug("templateFieldItems - {}", templateFieldItems);
    }


    @Test
    @DisplayName("Template 빌드 테스트")
    void testBuild() {
        final PayRequestDto dto = dummy();
        final Class<?> clazz = dto.getClass();

        final List<TemplateFieldMeta> templateFieldItems = getTemplate(clazz);
        final String body = build(templateFieldItems, dto);

        Assertions.assertEquals(dto.getLength(), body.length());

        log.debug("-- body length : {}", body.length());
        log.debug("-- body : {}", body);
    }

    private String build(List<TemplateFieldMeta> fields, Object object) {
        return fields.stream()
                .map(field -> {
                    String formatStr = formatter((TemplateField) field.getTemplateField(), getObjectByField(field.getField(), object));
                    log.debug("-- field : {} ({}) - ({}) {}", field.getField().getName(), ((TemplateField)field.getTemplateField()).len(), formatStr.length(), formatStr);
                    return formatter(((TemplateField)field.getTemplateField()), formatStr);
                })
                .collect(Collectors.joining());
    }

    private String getObjectByField(Field field, Object object) {
        try {
            field.setAccessible(true);
            Object value = field.get(object);

            if (value == null) {
                throw new RuntimeException("INVALID VALUE : " + field.getName());
            }

            return String.valueOf(value);
        } catch (IllegalAccessException e) {
            log.error("", e);
        }

        throw new RuntimeException("PARAMETER TEMPLATE ERROR");
    }

    private String formatter(TemplateField templateField, String obj) {
        obj = obj.trim();

        switch(templateField.formatter()) {
            case STRING:
            case DIGIT_L:
                return String.format("%-" + templateField.len() +"s", obj).replace(" ", "_");
            case DIGIT_FILL_0:
                return String.format("%" + templateField.len() +"s", obj).replace(" ", "0");
            case DIGIT_R:
                return String.format("%" + templateField.len() +"s", obj).replace(" ", "_");
        }

        throw new RuntimeException("NOT INITIALIZED FORMATTER TYPE");
    };

    private List<TemplateFieldMeta> getTemplate(Class<?> clazz) {
        if(cached.containsKey(clazz)) {
            log.debug("-- hit cached : {}", clazz);
            return cached.get(clazz);
        }

        final List<TemplateFieldMeta> templateFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(TemplateField.class))
                .map(field -> new TemplateFieldMeta(field, field.getDeclaredAnnotation(TemplateField.class)))
                .collect(Collectors.toList());

        templateFields.sort(Comparator.comparingInt(t -> ((TemplateField) t.getTemplateField()).ordered()));

        cached.put(clazz, templateFields);
        return templateFields;
    }

    public PayRequestDto dummy() {
        PayRequestDto dto = PayRequestDto.builder()
                .transactionType(TransactionType.PAYMENT)
                .transactionId("123456789a123456789b")
                .cardNo("1234567890123456")
                .cardExpMonth("1125")
                .cardCvc("777")
                .installmentMonth(String.format("%2d", 0))
                .payTransactionId("12345678901234567890")
                .price(110000L)
                .vat(10000L)
                .encryptedCardInfo("ENCRYPTCARDINFO")
                .ext("")
                .build();

        return dto;
    }

}