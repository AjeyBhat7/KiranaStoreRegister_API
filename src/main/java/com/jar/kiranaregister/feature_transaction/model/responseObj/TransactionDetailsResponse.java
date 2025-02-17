package com.jar.kiranaregister.feature_transaction.model.responseObj;

import lombok.Data;

import java.util.List;


@Data
public class TransactionDetailsResponse {

    private List<TransactionDetails> transactionDetails;
}
