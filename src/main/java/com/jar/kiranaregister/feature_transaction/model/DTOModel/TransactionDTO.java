package com.jar.kiranaregister.feature_transaction.model.DTOModel;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
public class TransactionDTO {

    private UUID id;
    private TransactionStatus status;
    private Date transactionTime;
    private Double amount;
    private CurrencyName currencyName;
    private TransactionType transactionType;
}
