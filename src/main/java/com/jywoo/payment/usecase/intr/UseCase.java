package com.jywoo.payment.usecase.intr;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface UseCase<T extends Request, R extends Response> {
    R apply(@Valid T t);
}
