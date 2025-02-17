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
import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.feature_transaction.service.BillService;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import com.jar.kiranaregister.feature_transaction.utils.TransactionUtils;
import com.jar.kiranaregister.feature_users.model.entity.UserInfo;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.jar.kiranaregister.feature_transaction.utils.TransactionUtils.mapToDTO;
import static com.jar.kiranaregister.feature_transaction.utils.TransactionUtils.mapToTransactionDetails;
import static com.jar.kiranaregister.utils.ValidationUtils.validateCurrency;
import static com.jar.kiranaregister.utils.ConversionUtils.*;

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
        log.info("Adding transaction for amount: {} and currency: {}", request.getAmount(), request.getCurrency());

        // Validate currency
        CurrencyName currencyName = validateCurrency(request.getCurrency());
        if (currencyName == null) {
            log.warn("Invalid currency: {}", request.getCurrency());
            throw new IllegalArgumentException("Invalid currency");
        }

        // Get the authenticated user
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Transaction initiated by user: {}", userInfo.getUserId());

        // Create and save transaction
        Transaction transaction = new Transaction();

        transaction.setUserId(userInfo.getUserId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyName(currencyName);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionTime(new Date());
        transaction.setBillId(billService.generateBillId(request.getPurchasedProducts()));

        transaction.setStatus(TransactionStatus.SUCCESSFUL);

        transactionDAO.save(transaction);
        log.info("Transaction saved successfully for user: {}", userInfo.getUserId());

//        evct reports from cache


        return TransactionStatus.SUCCESSFUL;
    }


    /**
     * creates the debit transaction
     * @param request
     * @return
     */
    @Override
    public TransactionStatus debitTransaction(DebitTransactionRequest request) {
        log.info("Adding transaction for amount: {} and currency: {}", request.getAmount(), request.getCurrency());

        // Validate currency
        CurrencyName currencyName = validateCurrency(request.getCurrency());
        if (currencyName == null) {
            log.warn("Invalid currency: {}", request.getCurrency());
            throw new IllegalArgumentException("Invalid currency");
        }

        // Get the authenticated user
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Transaction initiated by user: {}", userInfo.getUserId());

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(userInfo.getUsername());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyName(currencyName);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setTransactionTime(new Date());

        transactionDAO.save(transaction);
        log.info("Transaction saved successfully for user: {}", userInfo.getUserId());

        return TransactionStatus.SUCCESSFUL;
    }

    /**
     * gets the list of transaction
     * @param targetCurrency
     * @return transaction status
     */

    @Override
    public TransactionDetailsResponse getAllTransactions(String targetCurrency) {

        validateCurrency(targetCurrency);

        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Double> exchangeRates =  fxRatesService.getLatestFxRates().getRates();


        List<Transaction> transactions = transactionDAO.findByUserId(userInfo.getUserId());

        List<TransactionDetails> transactionDetailsList = transactions.stream().map(transaction -> {
            Bill bill = null;
            if (transaction.getTransactionType().equals(TransactionType.CREDIT)) {
                bill = billService.getBill(transaction.getBillId());
            }
            Bill convertedBill = convertBillCurrency(bill, targetCurrency);

            transaction.setAmount(getConvertedAmount(transaction.getAmount(), String.valueOf(transaction.getCurrencyName()),targetCurrency,exchangeRates));
            transaction.setCurrencyName(CurrencyName.valueOf(targetCurrency));
            return mapToTransactionDetails(transaction, convertedBill);

        }).collect(Collectors.toList());

        log.info("Retrieved {} transactions for user: {}", transactionDetailsList.size(), userInfo.getUserId());

        TransactionDetailsResponse transactionDetailsResponse = new TransactionDetailsResponse();
        transactionDetailsResponse.setTransactionDetails(transactionDetailsList);

        return transactionDetailsResponse;
    }



    /**
     * fetches the transaction by id and converts the amount to requested currency
     * @param id
     * @return trasaction dto
     */
    @Override
    public TransactionDTO getTransactionById(UUID id, String targetCurrency) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validateCurrency(targetCurrency);
        log.info("Fetching transaction with ID: {} for user: {}", id, userInfo.getUserId());

        Map<String, Double> exchangeRates =  fxRatesService.getLatestFxRates().getRates();


        return transactionDAO.findByTransactionIdAndUserId(id, userInfo.getUsername()).map(transaction -> {
                    transaction.setAmount(getConvertedAmount(transaction.getAmount(),transaction.getCurrencyName().name(),targetCurrency,exchangeRates));
                    return mapToDTO(transaction);
                })
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Transaction not found");
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
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Double> exchangeRates =  fxRatesService.getLatestFxRates().getRates();

        return transactionDAO.findByTransactionIdAndUserId(id, userInfo.getUserId()).map(transaction -> {
                    Bill bill = billService.getBill(transaction.getBillId());
                    Bill convertedBill = convertBillCurrency(bill, targetCurrency);

                    transaction.setAmount(getConvertedAmount(transaction.getAmount(),transaction.getCurrencyName().name(),targetCurrency,exchangeRates));
                    return mapToTransactionDetails(transaction, convertedBill);

                }).orElseThrow(() -> {
                    log.warn("Transaction not found with ID: {} for user: {}", id, userInfo.getUserId());
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
     * fetch the currency price from fxrates and returns the converted amount
     * @param amount
     * @param fromCurrency
     * @param targetCurrency
     * @return
     */




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
        Map<String, Double> exchangeRates =  fxRatesService.getLatestFxRates().getRates();
        List<PurchasedProductsDetails> convertedProducts = bill.getPurchasedProducts().stream()
                .map(product -> {
                    double convertedPrice = getConvertedAmount(product.getPrice(), String.valueOf(CurrencyName.USD), targetCurrency,exchangeRates);

                    PurchasedProductsDetails convertedProduct = new PurchasedProductsDetails();
                    convertedProduct.setProductId(product.getProductId());
                    convertedProduct.setQuantity(product.getQuantity());
                    convertedProduct.setPrice(convertedPrice);
                    return convertedProduct;
                })
                .collect(Collectors.toList());


        bill.setId(bill.getId());
        bill.setPurchasedProducts(convertedProducts);

        double convertedTotalAmount = getConvertedAmount(bill.getTotalAmount(), "USD", targetCurrency,exchangeRates);
        bill.setTotalAmount(convertedTotalAmount);

        return bill;
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
