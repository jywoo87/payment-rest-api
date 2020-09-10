package com.jywoo.payment.service;

import com.jywoo.payment.domain.Payment;
import com.jywoo.payment.domain.PriceVat;
import com.jywoo.payment.domain.Transaction;
import com.jywoo.payment.domain.enums.TransactionType;
import com.jywoo.payment.domain.repository.TransactionRepository;
import com.jywoo.payment.except.*;
import com.jywoo.payment.except.message.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction createPaymentTransaction(@Valid Payment payment, PriceVat priceVat) {
        final Transaction transaction = Transaction.build(TransactionType.PAYMENT, payment, priceVat);
        transaction.setRemainPriceVat(transaction.getPriceVat());

        checkDuplicatePaymentTransaction(transaction);

        return create(transaction);
    }

    private void checkDuplicatePaymentTransaction(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.PAYMENT) {
            long paymentTransactionCount = countAllByPaymentAndTransactionType(transaction.getPayment(), TransactionType.PAYMENT);
            if(paymentTransactionCount > 0) {
                throw new BadRequestException();
            }
        }
    }

    @Transactional
    public Transaction createCancelTransaction(String transactionId, PriceVat priceVat) {
        final Payment payment = getPaymentByTransactionId(transactionId);
        final Transaction transaction = Transaction.build(TransactionType.CANCEL, payment, priceVat);
        checkAvailableAndGetTransaction(transaction, priceVat);

        return create(transaction);
    }

    @Validated
    private Transaction create(@Valid Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    private Payment getPaymentByTransactionId(String transactionId) {
        final Transaction transaction = findById(transactionId);

        if(transaction.getTransactionType() != TransactionType.PAYMENT) {
            throw new BadRequestException(ExceptionMessage.INVALID_PAYMENT_TRANSACTION_ID);
        }

        return transaction.getPayment();
    }

    private void checkAvailableAndGetTransaction(Transaction transaction, PriceVat priceVat) {
        final Payment payment = transaction.getPayment();
        final List<Transaction> transactions = findAllByPayment(payment); //Payment의 기존 트랜잭션 리스트 가져옴
        final PriceVat remainPriceVat = remainPriceVatByTansactions(transactions);

        log.debug("-- checkAvailableCancel :: remainPriceVat : {}, cancelPriceVat : {}", remainPriceVat, priceVat);

        long remainPrice = remainPriceVat.getPrice() - priceVat.getPrice();
        if (remainPrice < 0) {
            throw new NotEnoughPriceException(transaction, remainPriceVat, priceVat);
        }

        long remainVat = remainPriceVat.getVat() - priceVat.getVat();
        if (remainVat < 0) {
            if(priceVat.isAutoCalcVat()) {
                priceVat = new PriceVat(priceVat.getPrice(), remainPriceVat.getVat());
            } else {
                throw new NotEnoughVatException(transaction, remainPriceVat, priceVat);
            }
        }

        if (remainPrice == 0 && remainVat > 0){
            throw new RemainVatException(transaction, remainPriceVat, priceVat);
        }

        transaction.setAutoCalcPriceVat(priceVat);
        transaction.setRemainPriceVat(PriceVat.minus(remainPriceVat, priceVat));
    }

    public Transaction findById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException());
    }

    private List<Transaction> findAllByPayment(Payment payment) {
        return transactionRepository.findAllByPayment(payment);
    }

    private long countAllByPaymentAndTransactionType(Payment payment, TransactionType transactionType) {
        return transactionRepository.countAllByPaymentAndTransactionType(payment, transactionType);
    }

    private PriceVat remainPriceVatByTansactions(List<Transaction> transactions) {
        final PriceVat priceVat = new PriceVat(0L, 0L);
        if(transactions == null || transactions.size() == 0) {
            return priceVat;
        }

        final long remainPrice = transactions.stream()
                .mapToLong(t-> t.getTransactionType() == TransactionType.PAYMENT ? t.getPriceVat().getPrice() : -1 * t.getPriceVat().getPrice())
                .sum();

        final long remainVat = transactions.stream()
                .mapToLong(t-> t.getTransactionType() == TransactionType.PAYMENT ? t.getPriceVat().getVat() : -1 * t.getPriceVat().getVat())
                .sum();

        return new PriceVat(remainPrice, remainVat);
    }
}
