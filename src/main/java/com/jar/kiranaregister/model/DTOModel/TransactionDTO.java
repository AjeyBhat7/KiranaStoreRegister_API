package com.jar.kiranaregister.model.DTOModel;

import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private UUID id;
    private TransactionStatus status;
    private Date transactionTime;
    private Double amount;
    private Currency currency;

    private TransactionType transactionType;

}
