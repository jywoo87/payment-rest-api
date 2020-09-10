package com.jywoo.payment.client.impl;

import com.jywoo.payment.client.PayClient;
import com.jywoo.payment.client.dto.PayRequestDto;
import com.jywoo.payment.client.template.TemplateBodyBuilder;
import com.jywoo.payment.client.template.supported.BodyLengthPrepender;
import com.jywoo.payment.client.template.supported.TemplateHeaderFinder;
import com.jywoo.payment.domain.PayRequest;
import com.jywoo.payment.service.PayRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
@Slf4j
public class PersistentPayClient extends PayClient<PayRequestDto, String> {
    private final PayRequestService payRequestService;
    private final TemplateHeaderFinder templateHeaderFinder;
    private final TemplateBodyBuilder bodyBuilder;
    private final BodyLengthPrepender bodyLengthPrepender;

    @Override
    public String send(PayRequestDto requestDto) {
        PayRequest payRequest = PayRequest.builder()
                                        .body(
                                                bodyLengthPrepender.apply(
                                                    templateHeaderFinder.apply(requestDto),
                                                    bodyBuilder.apply(requestDto)
                                                )
                                        )
                                        .build();

        log.debug("-- sendData : {}", payRequest.getBody());
        payRequestService.save(payRequest);
        return payRequest.getBody();
    }
}
