package com.jar.kiranaregister.service.serviceImplementation;

import static com.jar.kiranaregister.utils.TransactionUtils.mapToDTO;

import com.jar.kiranaregister.DAO.TransactionDAO;
import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.model.responseModel.FxRatesResponse;
import com.jar.kiranaregister.model.entity.Transaction;
import com.jar.kiranaregister.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.service.TransactionService;
import com.jar.kiranaregister.utils.TransactionUtils;
import com.jar.kiranaregister.utils.ValidationUtils;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImplementation implements TransactionService {

    private final TransactionDAO  transactionDAO;
    private final FxRatesService fxRatesService;

    @Autowired
    public TransactionServiceImplementation(TransactionDAO transactionDAO, FxRatesService fxRatesService) {
        this.transactionDAO = transactionDAO;
        this.fxRatesService = fxRatesService;
    }


    public TransactionStatus addTransaction(
            TransactionRequest request, TransactionType transactionType) {
        
        CurrencyName currencyName = ValidationUtils.validateCurrency(request.getCurrency());

        if (currencyName == null) {
            throw new IllegalArgumentException("Invalid currency");
        }
        
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Transaction transaction = new Transaction();
        
        transaction.setUserId(userDetails.getUsername());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyName(currencyName);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionTime(new Date());

        transactionDAO.save(transaction);

        return TransactionStatus.SUCCESSFUL;
    }

    @Override
    public List<TransactionDTO> getAllTransactions(String targetCurrency) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return transactionDAO.findByUserId(userDetails.getUsername()).stream()
                .map(transaction -> convertTransactionsCurrency(transaction, targetCurrency))
                .collect(Collectors.toList());
    }


    @Override
    public TransactionDTO getTransactionById(UUID id, String targetCurrency) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return transactionDAO
                .findByTransactionIdAndUserId(id,userDetails.getUsername())
                .map(transaction -> convertTransactionsCurrency(transaction, targetCurrency))
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    @Override
    public void deleteTransaction(UUID id) {
        if (!transactionDAO.existsById(id)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        transactionDAO.deleteById(id);
    }

    @Override
    public List<TransactionDTO> fetchTransactionsByInterval(Interval interval) {
        Date fromDate = getStartDate(interval);
        List<Transaction> transactions = transactionDAO.findByTransactionTimeAfter(fromDate);

        return transactions.stream().map(TransactionUtils::mapToDTO).collect(Collectors.toList());
    }

    //    helper functions

    private TransactionDTO convertTransactionsCurrency(
            Transaction transaction, String targetCurrency) {

        if (targetCurrency == null
                || targetCurrency.equalsIgnoreCase(String.valueOf(transaction.getCurrencyName()))) {
            return mapToDTO(transaction);
        }

        double convertedAmount = getConvertedAmount(transaction, targetCurrency);

        transaction.setAmount(convertedAmount);

        TransactionDTO transactionDTO = mapToDTO(transaction);

        transactionDTO.setCurrencyName(CurrencyName.valueOf(targetCurrency));
        return transactionDTO;
    }

    private double getConvertedAmount(Transaction transaction, String targetCurrency) {

        FxRatesResponse response = fxRatesService.getLatestFxRates();
        Map<String, Double> exchangeRates = response.getRates();

        if (!exchangeRates.containsKey(targetCurrency.toUpperCase())) {
            throw new IllegalArgumentException("Invalid currency type");
        }

        double usdAmount =
                transaction.getAmount()
                        / exchangeRates.get(String.valueOf(transaction.getCurrencyName()));
        return usdAmount * exchangeRates.get(targetCurrency.toUpperCase());
    }

    private Date getStartDate(Interval interval) {
        Calendar calendar = Calendar.getInstance();
        switch (interval) {
            case WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, -1);
            case MONTHLY -> calendar.add(Calendar.MONTH, -1);
            case YEARLY -> calendar.add(Calendar.YEAR, -1);
        }
        return calendar.getTime();
    }
}
