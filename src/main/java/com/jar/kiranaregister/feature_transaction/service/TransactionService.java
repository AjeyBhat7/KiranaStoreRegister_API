package com.jar.kiranaregister.feature_transaction.service;

import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDto;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CreditTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;

import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetails;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetailsResponse;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionStatusResponse;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    /**
     * creates a new transaction and generates the bill for purchased products
     *
     * @param request
     * @return
     */
    TransactionStatusResponse addTransaction(CreditTransactionRequest request);

    /**
     * gets the list of transaction
     *
     * @param request
     * @return transaction status
     */
    TransactionStatus debitTransaction(DebitTransactionRequest request);

    /**
     * fetch all the transactions then returns TransactionDetailsResponse object
     * @param currency
     * @return
     */
    TransactionDetailsResponse getAllTransactions(String currency);

    /**
     * fetches the transaction by id and converts the amount to requested currency
     *
     * @param id
     * @return trasaction dto
     */
    TransactionDto getTransactionById(UUID id, String currency);

    /**
     * fetch the transaction with bill containing all product details
     *
     * @param id
     * @param currency
     * @return
     */
    TransactionDetails getTransactionDetailsByTransactionId(UUID id, String currency);

    /**
     * deletes transaction by id
     * @param id
     */
    void deleteTransaction(UUID id);

    /**
     * fetch transactions with in the give time interval
     * @param interval
     * @return
     */
    List<TransactionDto> fetchTransactionsByInterval(Interval interval);
}
