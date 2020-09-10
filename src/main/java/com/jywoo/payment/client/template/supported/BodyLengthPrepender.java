package com.jywoo.payment.client.template.supported;

import com.jywoo.payment.client.template.annotations.TemplateHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class BodyLengthPrepender implements BiFunction<TemplateHeader, String, String> {
    @Override
    public String apply(TemplateHeader headerMeta, String body) {
        final BiFunction<Integer, String, String> formatter = headerMeta.type().getFormatter();
        final String bodyLength = String.valueOf(body.length());
        return Stream.of(formatter.apply(headerMeta.len(), bodyLength), body).collect(Collectors.joining());
    }
}
