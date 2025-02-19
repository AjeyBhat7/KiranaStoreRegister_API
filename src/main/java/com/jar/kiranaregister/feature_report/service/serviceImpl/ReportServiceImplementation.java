package com.jar.kiranaregister.feature_report.service.serviceImpl;

import static com.jar.kiranaregister.utils.CurrencyConversionUtils.getConvertedAmount;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.feature_fxrates.model.responseObj.FxRatesResponse;
import com.jar.kiranaregister.feature_fxrates.service.FxRatesService;
import com.jar.kiranaregister.feature_report.dao.ReportDao;
import com.jar.kiranaregister.feature_report.model.responseObj.ReportResponse;
import com.jar.kiranaregister.feature_report.service.ReportService;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDto;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CurrencyConversionRequest;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import com.jar.kiranaregister.utils.ValidationUtils;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReportServiceImplementation implements ReportService {

    private final TransactionService transactionService;
    private final FxRatesService fxRatesService;
    private final ReportDao reportDao;

    @Autowired
    public ReportServiceImplementation(
            TransactionService transactionService,
            FxRatesService fxRatesService,
            ReportDao reportDao) {
        this.transactionService = transactionService;
        this.fxRatesService = fxRatesService;
        this.reportDao = reportDao;
    }

    /**
     * Generates report for a given time interval and currency.
     *
     * @param interval Time period for which the report is generated.
     * @param currency Target currency for financial figures.
     */

    @Override
    public void generateReport(String interval, String currency) {
        log.info("Generating report for interval: {} and currency: {}", interval, currency);

        Interval requiredInterval = ValidationUtils.validateInterval(interval);
        CurrencyName requiredCurrencyName = ValidationUtils.validateCurrency(currency);

        // Fetch transactions for the given interval
        List<TransactionDto> transactions = transactionService.fetchTransactionsByInterval(requiredInterval);
        log.info("Fetched {} transactions for interval: {}", transactions.size(), interval);

        // Fetch latest FX rates
        Map<String, Double> exchangeRates = getLatestExchangeRates();

        // Convert all transactions to the requested currency
        convertTransactionsToCurrency(transactions, requiredCurrencyName, exchangeRates);

        // Calculate credit, debit, and net flow
        ReportResponse reportResponse = calculateReportMetrics(transactions);

        // Store the report in Redis
        reportDao.saveReport(interval, requiredCurrencyName.name(), reportResponse);
        log.info("Report saved to cache for interval: {} and currency: {}", interval, requiredCurrencyName.name());
    }

    /**
     * Fetches the latest exchange rates and ensures they are valid.
     */
    private Map<String, Double> getLatestExchangeRates() {
        FxRatesResponse response = fxRatesService.getLatestFxRates();
        if (response == null || response.getRates() == null) {
            log.error("Failed to fetch FX rates.");
            throw new ResourceNotFoundException("Failed to fetch FX rates.");
        }
        return response.getRates();
    }

    /**
     * Converts all transactions to the target currency using CurrencyConversionRequest.
     */
    private void convertTransactionsToCurrency(
            List<TransactionDto> transactions, CurrencyName targetCurrency, Map<String, Double> exchangeRates) {

        CurrencyConversionRequest conversionRequest = new CurrencyConversionRequest();
        conversionRequest.setToCurrency(targetCurrency.name());
        conversionRequest.setExchangeRates(exchangeRates);

        transactions.forEach(transaction -> {
            conversionRequest.setFromCurrency(transaction.getCurrencyName().toString());

            transaction.setAmount(getConvertedAmount(transaction.getAmount(), conversionRequest));
            transaction.setCurrencyName(targetCurrency);
        });
    }


    /**
     * Calculates credit, debit, and net flow for the report.
     */
    private ReportResponse calculateReportMetrics(List<TransactionDto> transactions) {
        double credit = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.CREDIT)
                .map(TransactionDto::getAmount)
                .reduce(0.0, Double::sum);

        double debit = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.DEBIT)
                .map(TransactionDto::getAmount)
                .reduce(0.0, Double::sum);

        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setCredits(credit);
        reportResponse.setDebits(debit);
        reportResponse.setNetFlow(credit - debit);
        reportResponse.setTransactions(transactions);

        log.info("Report generated. Credits: {}, Debits: {}, Net Flow: {}", credit, debit, credit - debit);

        return reportResponse;
    }


    /**
     * Fetches a previously generated report from the cache.
     *
     * @param interval Time period to generate report.
     * @param currency Target currency of the report.
     * @return Cached ReportDTO or null if not available.
     */
    @Override
    public ReportResponse fetchReport(String interval, String currency) {
        log.info("Fetching report for interval: {} and currency: {}", interval, currency);

        CurrencyName requiredCurrencyName = ValidationUtils.validateCurrency(currency);

        // Check cache
        ReportResponse report = reportDao.getReport(interval, requiredCurrencyName.name());
        if (report == null) {
            log.warn(
                    "Report not found : {} and currency: {}",
                    interval,
                    requiredCurrencyName.name());
            throw new ResourceNotFoundException("Report not found, Try again later");
        }

        log.info("Report fetched successfully.");
        return report;
    }
}
