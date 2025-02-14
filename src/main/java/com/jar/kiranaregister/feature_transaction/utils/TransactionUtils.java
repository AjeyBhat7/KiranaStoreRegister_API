package com.jar.kiranaregister.feature_transaction.utils;

import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDetails;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import java.util.Date;
import java.util.UUID;

public class TransactionUtils {

    public  static TransactionDTO mapToDTO(Transaction transaction) {

        TransactionDTO td = new TransactionDTO();

        td.setTransactionTime(transaction.getTransactionTime());
        td.setAmount(transaction.getAmount());
        td.setId(transaction.getTransactionId());
        td.setTransactionType(transaction.getTransactionType());
        td.setStatus(transaction.getStatus());
        td.setCurrencyName(transaction.getCurrencyName());

        return td;
    }



    public  Transaction mapToTransaction(TransactionDTO transactionDTO) {

        Transaction convertedTranssction = new Transaction();

        convertedTranssction.setAmount(transactionDTO.getAmount());
        convertedTranssction.setTransactionId(transactionDTO.getId());
        convertedTranssction.setTransactionType(transactionDTO.getTransactionType());
        convertedTranssction.setStatus(transactionDTO.getStatus());
        convertedTranssction.setTransactionTime(transactionDTO.getTransactionTime());
        return convertedTranssction;
    }

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

    public static Transaction createNewTransaction(
            UUID transactionId,
            TransactionStatus status,
            Double amount,
            String currency,
            TransactionType transactionType) {
        Transaction transaction = new Transaction();

        transaction.setTransactionId(transactionId);
        transaction.setStatus(status);
        transaction.setAmount(amount);
        transaction.setTransactionTime(new Date());

        return transaction;
    }
}
