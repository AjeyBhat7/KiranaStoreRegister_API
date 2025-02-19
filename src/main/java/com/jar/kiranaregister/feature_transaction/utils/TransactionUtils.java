package com.jar.kiranaregister.feature_transaction.utils;

import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDto;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetails;

public class TransactionUtils {

    /**
     * maps transactionDto to transaction
     *
     * @param transaction
     * @return
     */
    public static TransactionDto mapToDTO(Transaction transaction) {

        TransactionDto td = new TransactionDto();

        td.setTransactionTime(transaction.getTransactionTime());
        td.setAmount(transaction.getAmount());
        td.setId(transaction.getTransactionId());
        td.setTransactionType(transaction.getTransactionType());
        td.setStatus(transaction.getStatus());
        td.setCurrencyName(transaction.getCurrencyName());

        return td;
    }

    /**
     * maps transaction and bills to transactionDetails object
     *
     * @param transaction
     * @param bill
     * @return
     */
    public static TransactionDetails mapToTransactionDetails(Transaction transaction, Bill bill) {
        TransactionDetails td = new TransactionDetails();
        td.setTransactionTime(transaction.getTransactionTime());
        td.setAmount(transaction.getAmount());
        td.setId(transaction.getTransactionId());
        td.setTransactionType(transaction.getTransactionType());
        td.setStatus(transaction.getStatus());
        td.setCurrencyName(transaction.getCurrencyName());

        td.setBill(bill);

        return td;
    }
}
