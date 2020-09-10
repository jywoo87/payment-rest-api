package com.jywoo.payment.client.template.annotations.enums;

import lombok.Getter;

import java.util.function.BiFunction;

public enum TemplateFormatterType {
    STRING((len, obj) -> String.format("%-" + len.intValue() +"s", obj).replace(" ", "_")),
    DIGIT_L((len, obj) -> String.format("%-" + len.intValue() +"s", obj).replace(" ", "_")),
    DIGIT_R((len, obj) -> String.format("%" + len.intValue() +"s", obj).replace(" ", "_")),
    DIGIT_FILL_0((len, obj) -> String.format("%" + len.intValue() +"s", obj).replace(" ", "0"));

    @Getter
    private BiFunction<Integer, String, String> formatter;

    TemplateFormatterType(BiFunction<Integer, String, String> formatter) {
        this.formatter = formatter;
    }
}
