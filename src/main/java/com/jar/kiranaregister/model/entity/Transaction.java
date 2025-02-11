package com.jar.kiranaregister.model.entity;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private UUID transactionId;  // Ensure this is named correctly

    private String userId;


    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private Date transactionTime;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private CurrencyName currencyName;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
}
