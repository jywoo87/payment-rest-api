package com.jywoo.payment.api.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ErrorDto {
    public String requestUrl;
    public Long code;
    public List<String> errors;
}
