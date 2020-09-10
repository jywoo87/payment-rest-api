package com.jywoo.payment.api.controller;

import com.jywoo.payment.api.model.CancelResponseViewModel;
import com.jywoo.payment.api.model.PaymentResponseViewModel;
import com.jywoo.payment.api.model.SearchResponseViewModel;
import com.jywoo.payment.usecase.CancelUseCase;
import com.jywoo.payment.usecase.PaymentUseCase;
import com.jywoo.payment.usecase.SearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Validated
public class PaymentV1Controller {
    private final PaymentUseCase paymentUseCase;
    private final CancelUseCase cancelUseCase;
    private final SearchUseCase searchUseCase;

    @PostMapping("/payment")
    public PaymentResponseViewModel payment(@RequestBody @Valid PaymentUseCase.PaymentRequest paymentRequest) {
        return PaymentResponseViewModel.from(paymentUseCase.apply(paymentRequest));
    }

    @PostMapping("/cancel")
    public CancelResponseViewModel cancel(@RequestBody @Valid CancelUseCase.CancelRequest cancelRequest) {
        return CancelResponseViewModel.from(cancelUseCase.apply(cancelRequest));
    }

    @GetMapping("/search/{transactionId}")
    public SearchResponseViewModel search(@PathVariable(name = "transactionId") String transactionId) {
        return SearchResponseViewModel.from(searchUseCase.apply(new SearchUseCase.SearchRequest(transactionId)));
    }

}
