package com.jar.kiranaregister.model;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private UUID transactionId;

    //    private long customerId;

    //    private long billId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private Date transactionTime;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private CurrencyName currencyName;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Long customerId;
    //    private long refundDetailsId;
}
