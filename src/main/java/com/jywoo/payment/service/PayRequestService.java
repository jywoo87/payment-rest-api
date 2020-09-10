package com.jywoo.payment.service;

import com.jywoo.payment.domain.PayRequest;
import com.jywoo.payment.domain.repository.PayRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class PayRequestService {
    private final PayRequestRepository payRequestRepository;

    public PayRequest save(@Valid PayRequest payRequest) {
        return payRequestRepository.save(payRequest);
    }
}
