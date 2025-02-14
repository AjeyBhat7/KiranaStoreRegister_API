package com.jar.kiranaregister.feature_transaction.model.requestObj;

import lombok.*;

@Data
public class DebitTransactionRequest {
    private double amount;
    private String currency;


}
