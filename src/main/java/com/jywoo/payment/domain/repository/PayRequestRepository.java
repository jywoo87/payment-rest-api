package com.jywoo.payment.domain.repository;

import com.jywoo.payment.domain.PayRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayRequestRepository extends JpaRepository<PayRequest, Long> {
}
