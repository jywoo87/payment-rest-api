package com.jywoo.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "PAY_REQUEST")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PAYMENT_STR", length = 500)
    private String body;
}
