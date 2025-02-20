package com.jar.kiranaregister.feature_transaction.utils;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.feature_transaction.model.dto.TransactionDto;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CreditTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetails;
import com.jar.kiranaregister.feature_users.model.entity.UserInfo;
import java.util.Date;

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

    public static Transaction prepareCreditTransaction(
            CreditTransactionRequest request, UserInfo userInfo, CurrencyName currencyName) {
        // Create transaction
        Transaction transaction = new Transaction();

        transaction.setUserId(userInfo.getUserId());
        transaction.setCurrencyName(currencyName);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionTime(new Date());
        transaction.setAmount(request.getAmount());

        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        return transaction;
    }

    public static Transaction prepareDebitTransaction(
            DebitTransactionRequest request, UserInfo userInfo, CurrencyName currencyName) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userInfo.getUserId());
        transaction.setCurrencyName(currencyName);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setTransactionTime(new Date());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        return transaction;
    }
}
