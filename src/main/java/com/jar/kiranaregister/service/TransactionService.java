package com.jar.kiranaregister.service;


import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.model.requestObj.TransactionRequest;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionStatus addTransaction(TransactionRequest request, TransactionType transactionType);

    List<TransactionDTO> getAllTransactions(String currency);

    TransactionDTO getTransactionById(UUID id,String currency);

    void deleteTransaction(UUID id);

    List<TransactionDTO> fetchTransactionsByInterval(Interval interval);
}
