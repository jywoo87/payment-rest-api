package com.jywoo.payment.client.template.supported;

import com.jywoo.payment.client.template.annotations.TemplateHeader;
import com.jywoo.payment.except.InternalServiceException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Function;

@Component
public class TemplateHeaderFinder implements Function<Object, TemplateHeader> {
    @Override
    public TemplateHeader apply(Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(TemplateHeader.class))
                .findFirst()
                .map(field -> field.getDeclaredAnnotation(TemplateHeader.class))
                .orElseThrow(() -> new InternalServiceException());
    }
}
