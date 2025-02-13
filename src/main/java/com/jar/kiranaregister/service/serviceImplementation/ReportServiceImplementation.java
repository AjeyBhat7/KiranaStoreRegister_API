package com.jar.kiranaregister.service.serviceImplementation;


import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionType;

import com.jar.kiranaregister.DAO.ReportDao;
import com.jar.kiranaregister.model.DTOModel.ReportDTO;
import com.jar.kiranaregister.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.model.responseModel.FxRatesResponse;
import com.jar.kiranaregister.service.ReportService;
import com.jar.kiranaregister.service.TransactionService;
import com.jar.kiranaregister.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImplementation implements ReportService {

    private final TransactionService transactionService;
    private final FxRatesService fxRatesService;
    private final ReportDao reportDao;

    @Autowired
    public ReportServiceImplementation(TransactionService transactionService, FxRatesService fxRatesService, ReportDao reportDao) {
        this.transactionService = transactionService;
        this.fxRatesService = fxRatesService;
        this.reportDao = reportDao;
    }

    @Override
    public ReportDTO generateReport(String interval, String currency) {
        Interval requiredInterval = ValidationUtils.validateInterval(interval);
        CurrencyName requiredCurrencyName = ValidationUtils.validateCurrency(currency);

        // Fetch transactions for the given interval
        List<TransactionDTO> transactions = transactionService.fetchTransactionsByInterval(requiredInterval);

        // Fetch latest FX rates
        FxRatesResponse response = fxRatesService.getLatestFxRates();
        Map<String, Double> exchangeRates = response.getRates();

        // Convert all transactions to requested currency
        transactions.forEach(transaction -> {
            double convertedAmount = convertCurrency(transaction.getAmount(), transaction.getCurrencyName().toString(), requiredCurrencyName.name(), exchangeRates);
            transaction.setAmount(convertedAmount);
            transaction.setCurrencyName(requiredCurrencyName);
        });

        // Compute financial metrics
        double credit = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.CREDIT)
                .mapToDouble(TransactionDTO::getAmount)
                .sum();

        double debit = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.DEBIT)
                .mapToDouble(TransactionDTO::getAmount)
                .sum();

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setCredits(credit);
        reportDTO.setDebits(debit);
        reportDTO.setNetFlow(credit - debit);
        reportDTO.setTransactions(transactions);

        // Store the report in Redis
        reportDao.saveReport(interval,requiredCurrencyName.name(), reportDTO);

        return reportDTO;
    }

    @Override
    public ReportDTO fetchReport(String interval, String currency) {

        CurrencyName requiredCurrencyName = ValidationUtils.validateCurrency(currency);

        // Check cache first (stored in the requested currency)

        return reportDao.getReport(interval, currency);
    }

    
    private double convertCurrency(double amount, String fromCurrency, String toCurrency, Map<String, Double> exchangeRates) {
        if (fromCurrency.equals(toCurrency)) {
            return amount; // No conversion needed
        }
        if (!exchangeRates.containsKey(fromCurrency) || !exchangeRates.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Exchange rate not found for: " + fromCurrency + " or " + toCurrency);
        }
        double usdAmount = amount / exchangeRates.get(fromCurrency); // Convert to USD
        return usdAmount * exchangeRates.get(toCurrency); // Convert to target currency
    }
}
