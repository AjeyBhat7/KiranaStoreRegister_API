package com.jar.kiranaregister.feature_transaction.model.responseObj;

import com.jar.kiranaregister.enums.TransactionStatus;
import lombok.Data;

@Data
public class TransactionStatusResponse {
    private TransactionStatus transactionStatus;
}
