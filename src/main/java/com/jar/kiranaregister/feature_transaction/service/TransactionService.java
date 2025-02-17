package com.jar.kiranaregister.feature_transaction.service;

import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetails;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetailsResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionStatus addTransaction(TransactionRequest request);

    TransactionStatus debitTransaction(DebitTransactionRequest request);

    TransactionDetailsResponse getAllTransactions(String currency);

    TransactionDTO getTransactionById(UUID id, String currency);

    TransactionDetails getTransactionDetailsByTransactionId(UUID id, String currency);

    void deleteTransaction(UUID id);

    List<TransactionDTO> fetchTransactionsByInterval(Interval interval);
}
