package com.jywoo.payment.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jywoo.payment.util.crypto.StringMaskingUtil;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class MaskingCardNoSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(StringMaskingUtil.maskCardNo(value));
    }
}
