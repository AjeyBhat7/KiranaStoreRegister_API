package com.jar.kiranaregister.service.serviceImplementation;

import com.jar.kiranaregister.enums.Currency;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.model.DTOModel.ReportDTO;
import com.jar.kiranaregister.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.service.ReportService;
import com.jar.kiranaregister.service.TransactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImplementation implements ReportService {

    private final TransactionService transactionService;

    @Autowired
    public ReportServiceImplementation(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public ReportDTO generateReport(String interval) {

        Interval requiredInterval = validateInterval(interval);

        ReportDTO reportDTO = new ReportDTO();

        //        fetch transaction
        List<TransactionDTO> transactions =
                transactionService.fetchTransactionsByInterval(requiredInterval);

        double credit = 0;
        double debit = 0;

        for (TransactionDTO transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.CREDIT) {
                credit += transaction.getAmount();
            }
            if (transaction.getTransactionType() == TransactionType.DEBIT) {
                debit += transaction.getAmount();
            }
        }

        reportDTO.setCredits(credit);
        reportDTO.setDebits(debit);
        reportDTO.setNetFlow(credit - debit);
        reportDTO.setTransactions(transactions);

        return reportDTO;
    }

    @Override
    public ReportDTO fetchReport(String interval, String currency) {
        return generateReport(interval);
    }

    private Interval validateInterval(String interval) {

        try {
            return Interval.valueOf(interval.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid interval: " + interval);
        }
    }

    private Currency validateCurrency(String currency) {
        try {
            return Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }
}
