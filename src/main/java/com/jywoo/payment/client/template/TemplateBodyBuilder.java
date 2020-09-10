package com.jywoo.payment.client.template;

import com.jywoo.payment.client.template.annotations.TemplateField;
import com.jywoo.payment.client.template.annotations.enums.TemplateFormatterType;
import com.jywoo.payment.client.template.cache.TemplateFieldMeta;
import com.jywoo.payment.except.InternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.function.Function;

@Slf4j
@Component
public class TemplateBodyBuilder extends AbstractCachedTemplateBuilder<TemplateField> implements Function<Object, String> {

    TemplateBodyBuilder() {
        super(TemplateField.class);
    }

    @Override
    public String apply(Object instance) {
        return super.apply(instance);
    }

    @Override
    public String buildRow(TemplateFieldMeta<TemplateField> fieldMeta, Object instance) {
        final Field field = fieldMeta.getField();
        final TemplateField templateField = fieldMeta.getTemplateField();

        Object value = getValue(field, instance);

        if (value == null) {
            if(!templateField.nullable()) {
                throw new InternalServiceException();
            }
            value = "";
        }

        // TODO: primitype 이외 데이터 위한 converter 추가
        final String strValue = String.valueOf(value).trim();
        final TemplateFormatterType formatterType = templateField.formatter();

        return formatterType.getFormatter().apply(templateField.len(), strValue);
    }

    @Override
    public Comparator<TemplateFieldMeta<TemplateField>> getComparator() {
        return Comparator.comparingInt(o -> o.getTemplateField().ordered());
    }
}
