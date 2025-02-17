package com.jar.kiranaregister.feature_transaction.service.serviceImplementation;


import com.jar.kiranaregister.feature_fxrates.service.FxRatesService;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetailsResponse;
import com.jar.kiranaregister.feature_transaction.DAO.TransactionDAO;
import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.PurchasedProductsDetails;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetails;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_fxrates.model.responseObj.FxRatesResponse;
import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.feature_transaction.service.BillService;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import com.jar.kiranaregister.feature_transaction.utils.TransactionUtils;
import com.jar.kiranaregister.utils.ValidationUtils;
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


    /**
     * creates a new transaction and generates the bill for purchased products
     * @param request
     * @return
     */

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


    /**
     * creates the debit transaction
     * @param request
     * @return
     */
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

    /**
     * gets the list of transaction
     * @param targetCurrency
     * @return transaction status
     */

    @Override
    public TransactionDetailsResponse getAllTransactions(String targetCurrency) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Transaction> transactions = transactionDAO.findByUserId(userDetails.getUsername());

        List<TransactionDetails> transactionDetailsList = transactions.stream().map(transaction -> {
                 Bill bill = null;
                 if(transaction.getTransactionType().equals(TransactionType.CREDIT)) {
                        bill = billService.getBill(transaction.getBillId());
                 }
                 Bill convertedBill = convertBillCurrency(bill, targetCurrency);

                 Transaction convertedTransaction = convertTransactionCurrency(transaction, targetCurrency);
                 return mapToTransactionDetails(convertedTransaction, convertedBill);
        }).collect(Collectors.toList());

        log.info("Retrieved {} transactions for user: {}", transactionDetailsList.size(), userDetails.getUsername());

        TransactionDetailsResponse transactionDetailsResponse = new TransactionDetailsResponse();
        transactionDetailsResponse.setTransactionDetails(transactionDetailsList);

        return transactionDetailsResponse;
    }

    /**
     * fetches the transaction by id and converts the amount to requested currency
     * @param id
     * @param currency
     * @return trasaction dto
     */
    @Override
    public TransactionDTO getTransactionById(UUID id, String currency) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Fetching transaction with ID: {} for user: {}", id, userDetails.getUsername());

        return transactionDAO.findByTransactionIdAndUserId(id, userDetails.getUsername()).map(transaction -> {
                    Transaction convertedTransaction = convertTransactionCurrency(transaction, currency);
                    return mapToDTO(convertedTransaction);
                })
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Transaction not found");
                });
    }


    /**
     * fetch the transaction with bill containing all product details
     * @param id
     * @param targetCurrency
     * @return
     */

    @Override
    public TransactionDetails getTransactionDetailsByTransactionId(UUID id, String targetCurrency) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return transactionDAO.findByTransactionIdAndUserId(id, userDetails.getUsername()).map(transaction -> {
                    Bill bill = billService.getBill(transaction.getBillId());
                    Bill convertedBill = convertBillCurrency(bill, targetCurrency);
                    Transaction convertedTransaction = convertTransactionCurrency(transaction, targetCurrency);
                    return mapToTransactionDetails(convertedTransaction, convertedBill);
                }).orElseThrow(() -> {
                    log.warn("Transaction not found with ID: {} for user: {}", id, userDetails.getUsername());
                    return new IllegalArgumentException("Transaction not found");
                });
    }


    /**
     * delete transaction by id
     * @param id
     */
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

    /**
     * fetch the transaction by time interval
     * @param interval
     * @return
     */

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


    /**
     * convert transactions amount to target currency
     * @param transaction
     * @param targetCurrency
     * @return
     */

    private Transaction convertTransactionCurrency(Transaction transaction, String targetCurrency) {
        if (targetCurrency == null || targetCurrency.equalsIgnoreCase(transaction.getCurrencyName().name())) {
            return transaction;
        }

        double convertedAmount = getConvertedAmount(transaction.getAmount(), transaction.getCurrencyName(), targetCurrency);

        transaction.setAmount(convertedAmount);
        transaction.setCurrencyName(CurrencyName.valueOf(targetCurrency));

        return transaction;
    }

    /**
     * converts the each products amount to target currency
     * @param bill
     * @param targetCurrency
     * @return
     */
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


        bill.setId(bill.getId());
        bill.setPurchasedProducts(convertedProducts);

        double convertedTotalAmount = getConvertedAmount(bill.getTotalAmount(), CurrencyName.USD, targetCurrency);
        bill.setTotalAmount(convertedTotalAmount);

        return bill;
    }

    /**
     * fetch the currency price from fxrates and returns the converted amount
     * @param amount
     * @param fromCurrency
     * @param targetCurrency
     * @return
     */
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
