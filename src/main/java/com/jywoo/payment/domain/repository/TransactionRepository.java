package com.jywoo.payment.domain.repository;

import com.jywoo.payment.domain.Payment;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByPayment(Payment payment);
    long countAllByPaymentAndTransactionType(Payment payment, TransactionType transactionType);
}
