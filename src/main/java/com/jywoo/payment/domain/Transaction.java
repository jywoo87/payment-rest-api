package com.jywoo.payment.domain;

import com.jywoo.payment.domain.enums.TransactionType;
import com.jywoo.payment.domain.validation.annotation.PriceVatConstraint;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_TRANSACTION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "payment")
public class Transaction {
    @Id
    @GenericGenerator(name = "transactionId", strategy = "com.jywoo.payment.domain.supported.TransactionIdGenerator")
    @GeneratedValue(generator = "transactionId")
    @Column(name = "TRANSACTION_ID", length = 20)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "PAYMENT_ID")
    @NotNull
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", length = 10)
    private TransactionType transactionType;

    @Valid
    @PriceVatConstraint
    @Embedded
    private PriceVat priceVat;

    @Column(name = "CREATE_DT")
    private LocalDateTime createDate;

    public static Transaction build(TransactionType transactionType, Payment payment, PriceVat priceVat) {
        return Transaction.builder()
                            .transactionType(transactionType)
                            .payment(payment)
                            .priceVat(priceVat)
                            .createDate(LocalDateTime.now())
                            .build();
    }


    @Transient
    @Getter @Setter
    private PriceVat autoCalcPriceVat;

    @Transient
    @Getter @Setter
    private PriceVat remainPriceVat;

}
