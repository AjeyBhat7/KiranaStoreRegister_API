package com.jar.kiranaregister.service.serviceImplementation;

import static com.jar.kiranaregister.utils.TransactionUtils.mapToDTO;

import com.jar.kiranaregister.enums.Currency;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.model.FxRatesResponse;
import com.jar.kiranaregister.model.Transaction;
import com.jar.kiranaregister.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.repository.TransactionRepository;
import com.jar.kiranaregister.service.TransactionService;
import com.jar.kiranaregister.utils.TransactionUtils;
import java.util.*;
import java.util.stream.Collectors;

import com.jar.kiranaregister.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImplementation implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final FxRatesService fxRatesService;

    @Autowired
    public TransactionServiceImplementation(
            TransactionRepository transactionRepository, FxRatesService fxRatesService) {
        this.transactionRepository = transactionRepository;
        this.fxRatesService = fxRatesService;
    }



    public TransactionStatus addTransaction(
            TransactionRequest request, TransactionType transactionType) {
        Currency currency = ValidationUtils.validateCurrency(request.getCurrency());

        if (currency == null) {
            throw new IllegalArgumentException("Invalid currency");
        }

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setCurrency(currency);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionTime(new Date());

        transactionRepository.save(transaction);

        return TransactionStatus.SUCCESSFUL;
    }




    @Override
    public List<TransactionDTO> getAllTransactions(String targetCurrency) {

        return transactionRepository.findAll().stream()
                .map(transaction -> convertTransactionsCurrency(transaction, targetCurrency))
                .collect(Collectors.toList());
    }




    @Override
    public TransactionDTO getTransactionById(UUID id, String targetCurrency) {
        return transactionRepository
                .findById(id)
                .map(transaction -> convertTransactionsCurrency(transaction, targetCurrency))
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }




    @Override
    public void deleteTransaction(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }



    @Override
    public List<TransactionDTO> fetchTransactionsByInterval(Interval interval) {
        Date fromDate = getStartDate(interval);
        List<Transaction> transactions = transactionRepository.findByTransactionTimeAfter(fromDate);

        return transactions.stream().map(TransactionUtils::mapToDTO).collect(Collectors.toList());
    }



    //    helper functions

    private TransactionDTO convertTransactionsCurrency(
            Transaction transaction, String targetCurrency) {

        if (targetCurrency == null
                || targetCurrency.equalsIgnoreCase(String.valueOf(transaction.getCurrency()))) {
            return mapToDTO(transaction);
        }

        double convertedAmount = getConvertedAmount(transaction, targetCurrency);

        transaction.setAmount(convertedAmount);

        TransactionDTO transactionDTO = mapToDTO(transaction);

        transactionDTO.setCurrency(java.util.Currency.getInstance(targetCurrency));
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
                        / exchangeRates.get(String.valueOf(transaction.getCurrency()));
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
