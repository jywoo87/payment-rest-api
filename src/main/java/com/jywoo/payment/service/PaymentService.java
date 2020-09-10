package com.jywoo.payment.service;

import com.jywoo.payment.domain.Payment;
import com.jywoo.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment create(@Valid Payment payment) {
        return paymentRepository.save(payment);
    }
}
