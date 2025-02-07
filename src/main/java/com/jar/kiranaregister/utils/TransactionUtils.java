package com.jar.kiranaregister.utils;

import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.model.Transaction;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

public class TransactionUtils {

    public static TransactionDTO mapToDTO(Transaction transaction) {

        TransactionDTO td = new TransactionDTO();

        td.setTransactionTime(transaction.getTransactionTime());
        td.setAmount(transaction.getAmount());
        td.setId(transaction.getTransactionId());
        td.setTransactionType(transaction.getTransactionType());
        td.setStatus(transaction.getStatus());
        td.setCurrency(Currency.getInstance(transaction.getCurrency().name()));

        return td;
    }

    public static Transaction mapToTransaction(TransactionDTO transactionDTO) {

        Transaction convertedTranssction = new Transaction();

        convertedTranssction.setAmount(transactionDTO.getAmount());
        convertedTranssction.setTransactionId(transactionDTO.getId());
        convertedTranssction.setTransactionType(transactionDTO.getTransactionType());
        convertedTranssction.setStatus(transactionDTO.getStatus());
        convertedTranssction.setTransactionTime(transactionDTO.getTransactionTime());
        return convertedTranssction;
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
