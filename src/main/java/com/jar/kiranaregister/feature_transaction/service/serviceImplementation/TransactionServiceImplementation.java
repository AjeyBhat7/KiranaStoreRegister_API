package com.jar.kiranaregister.feature_transaction.service.serviceImplementation;


import com.jar.kiranaregister.feature_fxrates.service.FxRatesService;
import com.jar.kiranaregister.feature_transaction.DAO.TransactionDAO;
import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.PurchasedProductsDetails;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDetails;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_fxrates.model.responseObj.FxRatesResponse;
import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.feature_transaction.service.BillService;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import com.jar.kiranaregister.feature_transaction.utils.TransactionUtils;
import com.jar.kiranaregister.feature_transaction.utils.ValidationUtils;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static com.jar.kiranaregister.feature_transaction.utils.TransactionUtils.mapToDTO;
import static com.jar.kiranaregister.feature_transaction.utils.TransactionUtils.mapToTransactionDetails;

@Slf4j
@Service
public class TransactionServiceImplementation implements TransactionService {

    private final TransactionDAO transactionDAO;
    private final FxRatesService fxRatesService;
    private final BillService billService;

    @Autowired
    public TransactionServiceImplementation(TransactionDAO transactionDAO, FxRatesService fxRatesService, BillService billService) {
        this.transactionDAO = transactionDAO;
        this.fxRatesService = fxRatesService;
        this.billService = billService;
    }




    @Override
    public TransactionStatus addTransaction(TransactionRequest request) {
        log.info("Adding  transaction for amount: {} and currency: {}", request.getAmount(), request.getCurrency());

        // Validate currency
        CurrencyName currencyName = ValidationUtils.validateCurrency(request.getCurrency());
        if (currencyName == null) {
            log.warn("Invalid currency: {}", request.getCurrency());
            throw new IllegalArgumentException("Invalid currency");
        }

        // Get the authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Transaction initiated by user: {}", userDetails.getUsername());

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(userDetails.getUsername());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyName(currencyName);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionTime(new Date());

        transaction.setBillId(billService.generateBillId(request.getPurchasedProducts()));

        transactionDAO.save(transaction);
        log.info(" transaction saved successfully for user: {}", userDetails.getUsername());

        return TransactionStatus.SUCCESSFUL;
    }


    @Override
    public TransactionStatus debitTransaction(DebitTransactionRequest request) {
        log.info("Adding  transaction for amount: {} and currency: {}", request.getAmount(), request.getCurrency());

        // Validate currency
        CurrencyName currencyName = ValidationUtils.validateCurrency(request.getCurrency());
        if (currencyName == null) {
            log.warn("Invalid currency: {}", request.getCurrency());
            throw new IllegalArgumentException("Invalid currency");
        }

        // Get the authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Transaction initiated by user: {}", userDetails.getUsername());

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(userDetails.getUsername());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyName(currencyName);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionTime(new Date());

        transactionDAO.save(transaction);
        log.info(" transaction saved successfully for user: {}", userDetails.getUsername());

        return TransactionStatus.SUCCESSFUL;
    }


    @Override
    public List<TransactionDetails> getAllTransactions(String targetCurrency) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Transaction> transactions = transactionDAO.findByUserId(userDetails.getUsername());

        List<TransactionDetails> transactionDetailsList = transactions.stream()
                .map(transaction -> {
                    Bill bill = billService.getBill(transaction.getBillId());
                    Bill convertedBill = convertBillCurrency(bill, targetCurrency);
                    Transaction convertedTransaction = convertTransactionCurrency(transaction, targetCurrency);
                    return mapToTransactionDetails(convertedTransaction, convertedBill);
                })
                .collect(Collectors.toList());

        log.info("Retrieved {} transactions for user: {}", transactionDetailsList.size(), userDetails.getUsername());
        return transactionDetailsList;
    }

    @Override
    public TransactionDTO getTransactionById(UUID id, String currency) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Fetching transaction with ID: {} for user: {}", id, userDetails.getUsername());

        return transactionDAO.findByTransactionIdAndUserId(id, userDetails.getUsername())
                .map(transaction -> {
                    Transaction convertedTransaction = convertTransactionCurrency(transaction, currency);
                    return mapToDTO(convertedTransaction);
                })
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Transaction not found");
                });
    }


    @Override
    public TransactionDetails getTransactionDetailsByTransactionId(UUID id, String targetCurrency) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Fetching transaction with ID: {} for user: {} and target currency: {}", id, userDetails.getUsername(), targetCurrency);

        return transactionDAO.findByTransactionIdAndUserId(id, userDetails.getUsername())
                .map(transaction -> {
                    Bill bill = billService.getBill(transaction.getBillId());
                    Bill convertedBill = convertBillCurrency(bill, targetCurrency);
                    Transaction convertedTransaction = convertTransactionCurrency(transaction, targetCurrency);
                    return mapToTransactionDetails(convertedTransaction, convertedBill);
                })
                .orElseThrow(() -> {
                    log.warn("Transaction not found with ID: {} for user: {}", id, userDetails.getUsername());
                    return new IllegalArgumentException("Transaction not found");
                });
    }



    @Override
    public void deleteTransaction(UUID id) {
        log.info("Request to delete transaction with ID: {}", id);

        if (!transactionDAO.existsById(id)) {
            log.warn("Transaction not found with ID: {}", id);
            throw new IllegalArgumentException("Transaction not found");
        }

        transactionDAO.deleteById(id);
        log.info("Transaction with ID: {} deleted successfully", id);
    }

    @Override
    public List<TransactionDTO> fetchTransactionsByInterval(Interval interval) {
        log.info("Fetching transactions for interval: {}", interval);

        Date fromDate = getStartDate(interval);
        List<Transaction> transactions = transactionDAO.findByTransactionTimeAfter(fromDate);

        log.info("Retrieved {} transactions for interval: {}", transactions.size(), interval);
        return transactions.stream()
                .map(TransactionUtils::mapToDTO)
                .collect(Collectors.toList());
    }

    private Transaction convertTransactionCurrency(Transaction transaction, String targetCurrency) {
        if (targetCurrency == null || targetCurrency.equalsIgnoreCase(transaction.getCurrencyName().name())) {
            return transaction;
        }

        double convertedAmount = getConvertedAmount(transaction.getAmount(), transaction.getCurrencyName(), targetCurrency);

        Transaction convertedTransaction = new Transaction();
        convertedTransaction.setTransactionId(transaction.getTransactionId());
        convertedTransaction.setUserId(transaction.getUserId());
        convertedTransaction.setAmount(convertedAmount);
        convertedTransaction.setCurrencyName(CurrencyName.valueOf(targetCurrency));
        convertedTransaction.setTransactionType(transaction.getTransactionType());
        convertedTransaction.setStatus(transaction.getStatus());
        convertedTransaction.setTransactionTime(transaction.getTransactionTime());
        convertedTransaction.setBillId(transaction.getBillId());

        return convertedTransaction;
    }

    private Bill convertBillCurrency(Bill bill, String targetCurrency) {
        if (bill == null || targetCurrency == null || bill.getPurchasedProducts().isEmpty()) {
            return bill;
        }

        List<PurchasedProductsDetails> convertedProducts = bill.getPurchasedProducts().stream()
                .map(product -> {
                    double convertedPrice = getConvertedAmount(product.getPrice(), CurrencyName.USD, targetCurrency);
                    PurchasedProductsDetails convertedProduct = new PurchasedProductsDetails();
                    convertedProduct.setProductId(product.getProductId());
                    convertedProduct.setQuantity(product.getQuantity());
                    convertedProduct.setPrice(convertedPrice);
                    return convertedProduct;
                })
                .collect(Collectors.toList());

        Bill convertedBill = new Bill();
        convertedBill.setBillId(bill.getBillId());
        convertedBill.setPurchasedProducts(convertedProducts);

        double convertedTotalAmount = getConvertedAmount(bill.getTotalAmount(), CurrencyName.USD, targetCurrency);
        convertedBill.setTotalAmount(convertedTotalAmount);

        return convertedBill;
    }

    private double getConvertedAmount(double amount, CurrencyName fromCurrency, String targetCurrency) {
        FxRatesResponse response = fxRatesService.getLatestFxRates();
        Map<String, Double> exchangeRates = response.getRates();

        if (!exchangeRates.containsKey(targetCurrency.toUpperCase())) {
            throw new IllegalArgumentException("Invalid currency type");
        }

        double usdAmount = amount / exchangeRates.get(fromCurrency.name());
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
