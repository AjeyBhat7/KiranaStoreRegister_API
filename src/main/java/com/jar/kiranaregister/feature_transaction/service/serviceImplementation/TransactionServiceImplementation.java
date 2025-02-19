package com.jar.kiranaregister.feature_transaction.service.serviceImplementation;

import static com.jar.kiranaregister.feature_transaction.utils.TransactionUtils.mapToDTO;
import static com.jar.kiranaregister.feature_transaction.utils.TransactionUtils.mapToTransactionDetails;
import static com.jar.kiranaregister.utils.CurrencyConversionUtils.*;
import static com.jar.kiranaregister.utils.DateUtils.getStartDate;
import static com.jar.kiranaregister.utils.ValidationUtils.validateCurrency;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.feature_fxrates.service.FxRatesService;
import com.jar.kiranaregister.feature_transaction.dao.TransactionDao;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDto;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CreditTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CurrencyConversionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.GenerateBillRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.BillResponse;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetails;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetailsResponse;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionStatusResponse;
import com.jar.kiranaregister.feature_transaction.service.BillService;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import com.jar.kiranaregister.feature_transaction.utils.TransactionUtils;
import com.jar.kiranaregister.feature_users.model.entity.UserInfo;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionServiceImplementation implements TransactionService {

    private final TransactionDao transactionDAO;
    private final FxRatesService fxRatesService;
    private final BillService billService;

    @Autowired
    public TransactionServiceImplementation(
            TransactionDao transactionDAO, FxRatesService fxRatesService, BillService billService) {
        this.transactionDAO = transactionDAO;
        this.fxRatesService = fxRatesService;
        this.billService = billService;
    }

    /**
     * creates a new transaction and generates the bill for purchased products
     *
     * @param request
     * @return
     */
    @Override
    public TransactionStatusResponse addTransaction(CreditTransactionRequest request) {
        log.info(
                "Adding transaction for amount: {} and currency: {}",
                request.getAmount(),
                request.getCurrency());

        // Validate currency
        CurrencyName currencyName = validateCurrency(request.getCurrency());
        if (currencyName == null) {
            log.warn("Invalid currency: {}", request.getCurrency());
            throw new IllegalArgumentException("Invalid currency");
        }

        // Get the authenticated user
        UserInfo userInfo =
                (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Transaction initiated by user: {}", userInfo.getUserId());

        // Create and save transaction
        Transaction transaction = new Transaction();

        transaction.setUserId(userInfo.getUserId());
        transaction.setCurrencyName(currencyName);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionTime(new Date());
        transaction.setAmount(request.getAmount());

        GenerateBillRequest generateBillRequest = new GenerateBillRequest();
        generateBillRequest.setAmount(request.getAmount());
        generateBillRequest.setPurchasedProductsList(request.getPurchasedProducts());

        // Generate Bill
        BillResponse newBill = billService.generateBillId(generateBillRequest);
        transaction.setBillId(newBill.getBillId());


        transaction.setStatus(TransactionStatus.SUCCESSFUL);

        transactionDAO.save(transaction);
        log.info("Transaction saved successfully for user: {}", userInfo.getUserId());

        TransactionStatusResponse transactionStatusResponse = new TransactionStatusResponse();
        transactionStatusResponse.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        return transactionStatusResponse;
    }

    /**
     * creates the debit transaction
     *
     * @param request
     * @return
     */
    @Override
    public TransactionStatus debitTransaction(DebitTransactionRequest request) {
        log.info(
                "Adding transaction for amount: {} and currency: {}",
                request.getAmount(),
                request.getCurrency());

        // Validate currency
        CurrencyName currencyName = validateCurrency(request.getCurrency());
        if (currencyName == null) {
            log.warn("Invalid currency: {}", request.getCurrency());
            throw new IllegalArgumentException("Invalid currency");
        }

        // Get the authenticated user
        UserInfo userInfo =
                (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(userInfo.getUsername());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyName(currencyName);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setTransactionTime(new Date());

        transactionDAO.save(transaction);
        log.info(MessageFormat.format("Transaction saved successfully for user: {0}", userInfo.getUserId()));

        return TransactionStatus.SUCCESSFUL;
    }

    /**
     * gets the list of transaction
     *
     * @param targetCurrency
     * @return transaction status
     */
    @Override
    public TransactionDetailsResponse getAllTransactions(String targetCurrency) {

        if(targetCurrency != null) {
            validateCurrency(targetCurrency);
        }

        UserInfo userInfo =
                (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Double> exchangeRates = fxRatesService.getLatestFxRates().getRates();

        List<Transaction> transactions = transactionDAO.findByUserId(userInfo.getUserId());

        List<TransactionDetails> transactionDetailsList =
                transactions.stream()
                        .map(
                                transaction -> {
                                    return getTransactionDetails(
                                            targetCurrency, transaction, exchangeRates);
                                })
                        .toList();

        log.info(
                "Retrieved {} transactions for user: {}",
                transactionDetailsList.size(),
                userInfo.getUserId());

        TransactionDetailsResponse transactionDetailsResponse = new TransactionDetailsResponse();
        transactionDetailsResponse.setTransactionDetails(transactionDetailsList);

        return transactionDetailsResponse;
    }

    private TransactionDetails getTransactionDetails(
            String targetCurrency, Transaction transaction, Map<String, Double> exchangeRates) {

        Bill bill = (transaction.getTransactionType() == TransactionType.CREDIT)
                ? billService.getBill(transaction.getBillId()) : null;

        if(targetCurrency != null) {

            if(bill != null) {
                convertBillCurrency(targetCurrency, transaction, exchangeRates, bill);
            }

            CurrencyConversionRequest ccRequest = new CurrencyConversionRequest();
            ccRequest.setExchangeRates(exchangeRates);
            ccRequest.setFromCurrency(transaction.getCurrencyName().name());
            ccRequest.setToCurrency(targetCurrency);

            transaction.setAmount(getConvertedAmount(transaction.getAmount(),ccRequest));

            transaction.setCurrencyName(CurrencyName.valueOf(targetCurrency));
        }

        return mapToTransactionDetails(transaction, bill);
    }

    /**
     * fetches the transaction by id and converts the amount to requested currency
     *
     * @param id
     * @return trasaction dto
     */
    @Override
    public TransactionDto getTransactionById(UUID id, String targetCurrency) {
        UserInfo userInfo =
                (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validateCurrency(targetCurrency);
        log.info("Fetching transaction with ID: {} for user: {}", id, userInfo.getUserId());

        Map<String, Double> exchangeRates = fxRatesService.getLatestFxRates().getRates();



        Transaction transaction = transactionDAO.findByTransactionIdAndUserId(id, userInfo.getUsername()).orElse(null);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction not found");
        }

        CurrencyConversionRequest ccRequest = new CurrencyConversionRequest();
        ccRequest.setExchangeRates(exchangeRates);
        ccRequest.setFromCurrency(transaction.getCurrencyName().name());
        ccRequest.setToCurrency(targetCurrency);

        transaction.setAmount(getConvertedAmount(transaction.getAmount(),ccRequest));
        transaction.setCurrencyName(CurrencyName.valueOf(targetCurrency));

        return mapToDTO(transaction);
    }

    /**
     * fetch the transaction with bill containing all product details
     *
     * @param id
     * @param targetCurrency
     * @return
     */
    @Override
    public TransactionDetails getTransactionDetailsByTransactionId(UUID id, String targetCurrency) {
        UserInfo userInfo =
                (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Double> exchangeRates = fxRatesService.getLatestFxRates().getRates();

         TransactionDetails transactionDetails =transactionDAO
                .findByTransactionIdAndUserId(id, userInfo.getUserId())
                .map(
                        transaction -> {
                            return getDetails(targetCurrency, transaction, exchangeRates);
                        })
                .orElse(null);

         if(transactionDetails == null) {
             log.warn("Transaction not found with ID: {} for user: {}", id, userInfo.getUserId());

             throw new ResourceNotFoundException("Transaction not found");
         }

         return transactionDetails;

    }


    /**
     * fetch the bill from billService
     * converts the currency to target currency
     * @param targetCurrency
     * @param transaction
     * @param exchangeRates
     * @return
     */
    private TransactionDetails getDetails(String targetCurrency, Transaction transaction, Map<String, Double> exchangeRates) {

        Bill bill = billService.getBill(transaction.getBillId());

        convertBillCurrency(targetCurrency, transaction, exchangeRates, bill);

        CurrencyConversionRequest currencyConversionRequest = new CurrencyConversionRequest();

        transaction.setAmount(getConvertedAmount(transaction.getAmount(),currencyConversionRequest));

        return mapToTransactionDetails(transaction, bill);
    }


    private void convertBillCurrency(String targetCurrency, Transaction transaction, Map<String, Double> exchangeRates, Bill bill) {
        CurrencyConversionRequest currencyConversionRequest = new CurrencyConversionRequest();

        currencyConversionRequest.setFromCurrency(transaction.getCurrencyName().name());
        currencyConversionRequest.setToCurrency(targetCurrency);
        currencyConversionRequest.setExchangeRates(exchangeRates);

        bill.getPurchasedProducts().forEach(product -> {
            product.setPrice(getConvertedAmount(product.getPrice(),currencyConversionRequest));
        });

        bill.setTotalAmount(getConvertedAmount(bill.getTotalAmount(),currencyConversionRequest));
    }


    /**
     * delete transaction by id
     *
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
     *
     * @param interval
     * @return
     */
    @Override
    public List<TransactionDto> fetchTransactionsByInterval(Interval interval) {
        log.info("Fetching transactions for interval: {}", interval);

        Date fromDate = getStartDate(interval);
        List<Transaction> transactions = transactionDAO.findByTransactionTimeAfter(fromDate);

        log.info("Retrieved {} transactions for interval: {}", transactions.size(), interval);
        return transactions.stream().map(TransactionUtils::mapToDTO).collect(Collectors.toList());
    }


}
