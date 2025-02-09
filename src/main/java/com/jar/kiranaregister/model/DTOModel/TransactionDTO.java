package com.jar.kiranaregister.model.DTOModel;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private UUID id;
    private TransactionStatus status;
    private Date transactionTime;
    private Double amount;
    private CurrencyName currencyName;

    private TransactionType transactionType;
}
