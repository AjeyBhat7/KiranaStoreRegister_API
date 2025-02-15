package com.jar.kiranaregister.feature_report.service.serviceImpl;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.feature_report.dao.ReportDao;
import com.jar.kiranaregister.feature_report.model.dto.ReportDTO;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.feature_fxrates.model.responseObj.FxRatesResponse;
import com.jar.kiranaregister.feature_report.service.ReportService;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import com.jar.kiranaregister.feature_fxrates.service.FxRatesService;
import com.jar.kiranaregister.utils.ValidationUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
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

    /**
     * Generates  report for a given time interval and currency.
     *
     * @param interval Time period for which the report is generated.
     * @param currency Target currency for financial figures.
     * @return ReportDTO containing credits, debits, net flow, and transaction details.
     */
    @Override
    public ReportDTO generateReport(String interval, String currency) {
        log.info("Generating report for interval: {} and currency: {}", interval, currency);

        Interval requiredInterval = ValidationUtils.validateInterval(interval);
        CurrencyName requiredCurrencyName = ValidationUtils.validateCurrency(currency);

        // Fetch transactions for the given interval
        List<TransactionDTO> transactions = transactionService.fetchTransactionsByInterval(requiredInterval);
        log.info("Fetched {} transactions for interval: {}", transactions.size(), interval);

        // Fetch latest FX rates
        FxRatesResponse response = fxRatesService.getLatestFxRates();
        if (response == null || response.getRates() == null) {
            log.error("Failed to fetch FX rates. Returning null report.");
            return null;
        }
        Map<String, Double> exchangeRates = response.getRates();

        // Convert all transactions to requested currency
        transactions.forEach(transaction -> {
            double convertedAmount = convertCurrency(transaction.getAmount(), transaction.getCurrencyName().toString(), requiredCurrencyName.name(), exchangeRates);
            transaction.setAmount(convertedAmount);
            transaction.setCurrencyName(requiredCurrencyName);
        });

        // Compute financial metrics using reduce()
        double credit = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.CREDIT)
                .map(TransactionDTO::getAmount)
                .reduce(0.0, Double::sum);


        double debit = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.DEBIT)
                .map(TransactionDTO::getAmount)
                .reduce(0.0, Double::sum);


        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setCredits(credit);
        reportDTO.setDebits(debit);
        reportDTO.setNetFlow(credit - debit);
        reportDTO.setTransactions(transactions);

        log.info("Report generated. Credits: {}, Debits: {}, Net Flow: {}", credit, debit, credit - debit);

        // Store the report in Redis
        reportDao.saveReport(interval, requiredCurrencyName.name(), reportDTO);
        log.info("Report saved to cache for interval: {} and currency: {}", interval, requiredCurrencyName.name());

        return reportDTO;
    }

    /**
     * Fetches a previously generated report from the cache.
     *
     * @param interval Time period to generate report.
     * @param currency Target currency of the report.
     * @return Cached ReportDTO or null if not available.
     */
    @Override
    public ReportDTO fetchReport(String interval, String currency) {
        log.info("Fetching report for interval: {} and currency: {}", interval, currency);

        CurrencyName requiredCurrencyName = ValidationUtils.validateCurrency(currency);

        // Check cache first (stored in the requested currency)
        ReportDTO report = reportDao.getReport(interval, requiredCurrencyName.name());
        if (report == null) {
            log.warn("Report not found : {} and currency: {}", interval, requiredCurrencyName.name());
            throw new NoSuchElementException("Report not found");
        } else {
            log.info("Report fetched successfully from cache.");
        }
        return report;
    }

    /**
     * Converts an amount from one currency to another based on the provided exchange rates.
     *
     * @param amount       The amount to convert.
     * @param fromCurrency The original currency.
     * @param toCurrency   The target currency.
     * @param exchangeRates A map of exchange rates.
     * @return The converted amount.
     */
    private double convertCurrency(double amount, String fromCurrency, String toCurrency, Map<String, Double> exchangeRates) {
        if (fromCurrency.equals(toCurrency)) {
            return amount; // No conversion needed
        }
        if (!exchangeRates.containsKey(fromCurrency) || !exchangeRates.containsKey(toCurrency)) {
            log.error("Exchange rate not found for: {} or {}", fromCurrency, toCurrency);
            throw new IllegalArgumentException("Exchange rate not found for: " + fromCurrency + " or " + toCurrency);
        }
        double usdAmount = amount / exchangeRates.get(fromCurrency); // Convert to USD
        return usdAmount * exchangeRates.get(toCurrency); // Convert to target currency
    }
}
