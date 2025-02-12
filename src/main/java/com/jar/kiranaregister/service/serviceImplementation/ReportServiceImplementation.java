package com.jar.kiranaregister.service.serviceImplementation;

import static com.jar.kiranaregister.utils.ValidationUtils.validateCurrency;
import static com.jar.kiranaregister.utils.ValidationUtils.validateInterval;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.model.DTOModel.ReportDTO;
import com.jar.kiranaregister.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.model.responseModel.FxRatesResponse;
import com.jar.kiranaregister.service.ReportService;
import com.jar.kiranaregister.service.TransactionService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImplementation implements ReportService {

    private final TransactionService transactionService;
    private final FxRatesService fxRatesService;

    @Autowired
    public ReportServiceImplementation(
            TransactionService transactionService, FxRatesService fxRatesService) {
        this.transactionService = transactionService;
        this.fxRatesService = fxRatesService;
    }

    @Override
    public ReportDTO generateReport(String interval, String currency) {

        Interval requiredInterval = validateInterval(interval);
        CurrencyName requiredCurrencyName = validateCurrency(currency);

        // Fetch transactions for the given interval
        List<TransactionDTO> transactions =
                transactionService.fetchTransactionsByInterval(requiredInterval);

        FxRatesResponse response = fxRatesService.getLatestFxRates();
        Map<String, Double> exchangeRates = response.getRates();

        if (!exchangeRates.containsKey(requiredCurrencyName.name())) {
            throw new IllegalArgumentException("Invalid currency type: " + requiredCurrencyName);
        }

        transactions.forEach(
                t -> {
                    double convertedAmount =
                            getConvertedAmount(t, exchangeRates, requiredCurrencyName.name());
                    t.setAmount(convertedAmount);
                    t.setCurrencyName(requiredCurrencyName); // Fixed update
                });

        // Compute credit, debit, and net flow
        double credit =
                transactions.stream()
                        .filter(t -> t.getTransactionType() == TransactionType.CREDIT)
                        .mapToDouble(TransactionDTO::getAmount)
                        .sum();

        double debit =
                transactions.stream()
                        .filter(t -> t.getTransactionType() == TransactionType.DEBIT)
                        .mapToDouble(TransactionDTO::getAmount)
                        .sum();

        // Construct and return the report
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setCredits(credit);
        reportDTO.setDebits(debit);
        reportDTO.setNetFlow(credit - debit);
        reportDTO.setTransactions(transactions);

        return reportDTO;
    }

    @Override
    public ReportDTO fetchReport(String interval, String currency) {
        return generateReport(interval, currency);
    }

    private double getConvertedAmount(
            TransactionDTO transaction, Map<String, Double> exchangeRates, String targetCurrency) {
        String transactionCurrency = transaction.getCurrencyName().toString();

        if (!exchangeRates.containsKey(transactionCurrency)) {
            throw new IllegalArgumentException(
                    "Exchange rate not found for: " + transactionCurrency);
        }

        double usdAmount = transaction.getAmount() / exchangeRates.get(transactionCurrency);
        return usdAmount * exchangeRates.get(targetCurrency);
    }
}
