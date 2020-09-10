package com.jywoo.payment.client.template;

import com.jywoo.payment.client.template.annotations.TemplateHeader;
import com.jywoo.payment.client.template.annotations.enums.TemplateFormatterType;
import com.jywoo.payment.client.template.cache.TemplateFieldMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.function.Function;

@Slf4j
@Component
public class TemplateHeaderBuilder extends AbstractCachedTemplateBuilder<TemplateHeader> implements Function<Object, String> {

    public TemplateHeaderBuilder() {
        super(TemplateHeader.class);
    }

    @Override
    public String apply(Object instance) {
        return super.apply(instance);
    }

    @Override
    public String buildRow(TemplateFieldMeta<TemplateHeader> fieldMeta, Object instance) {
        final Field field = fieldMeta.getField();
        final TemplateHeader templateField = fieldMeta.getTemplateField();

        Object value = getValue(field, instance);

        // TODO: primitype 이외 데이터 위한 converter 추가
        final String strValue = String.valueOf(value).trim();
        final TemplateFormatterType formatterType = templateField.type();

        return formatterType.getFormatter().apply(templateField.len(), strValue);
    }

    @Override
    public Comparator<TemplateFieldMeta<TemplateHeader>> getComparator() {
        return null;
    }
}
