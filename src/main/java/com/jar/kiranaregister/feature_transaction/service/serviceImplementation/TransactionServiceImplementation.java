package com.jar.kiranaregister.feature_transaction.service.serviceImplementation;

import static com.jar.kiranaregister.feature_transaction.constants.TransactionConstants.*;
import static com.jar.kiranaregister.feature_transaction.utils.TransactionUtils.*;
import static com.jar.kiranaregister.utils.CurrencyConversionUtils.*;
import static com.jar.kiranaregister.utils.DateUtils.getStartDate;
import static com.jar.kiranaregister.utils.ValidationUtils.validateCurrency;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.feature_fxrates.service.FxRatesService;
import com.jar.kiranaregister.feature_transaction.dao.TransactionDao;
import com.jar.kiranaregister.feature_transaction.model.dto.TransactionDto;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CreditTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CurrencyConversionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.GenerateBillRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.BillResponse;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetails;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetailsResponse;
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
    public TransactionStatus addTransaction(CreditTransactionRequest request) {
        log.info(
                MessageFormat.format(
                        LOG_ADD_TRANSACTION, request.getAmount(), request.getCurrency()));

        // Validate currency
        CurrencyName currencyName = validateCurrency(request.getCurrency());
        if (currencyName == null) {
            log.warn(MessageFormat.format(LOG_INVALID_CURRENCY, request.getCurrency()));
            throw new IllegalArgumentException("Invalid currency");
        }

        // Get the authenticated user
        UserInfo userInfo =
                (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info(MessageFormat.format(LOG_TRANSACTION_INITIATED_BY_USER, userInfo.getUserId()));

        Transaction transaction = prepareCreditTransaction(request, userInfo, currencyName);

        // Generate Bill
        GenerateBillRequest generateBillRequest = new GenerateBillRequest();
        generateBillRequest.setAmount(request.getAmount());
        generateBillRequest.setPurchasedProductsList(request.getPurchasedProducts());

        BillResponse newBill = billService.generateBillId(generateBillRequest);
        transaction.setBillId(newBill.getBillId());

        transactionDAO.save(transaction);
        log.info(MessageFormat.format(LOG_TRANSACTION_SAVED_SUCCESSFULLY, userInfo.getUserId()));

        return TransactionStatus.SUCCESSFUL;
    }

    /**
     * creates the debit transaction
     *
     * @param request DebitTransactionRequest
     * @return
     */
    @Override
    public TransactionStatus debitTransaction(DebitTransactionRequest request) {
        log.info(
                MessageFormat.format(
                        LOG_ADD_TRANSACTION, request.getAmount(), request.getCurrency()));

        // Validate currency
        CurrencyName currencyName = validateCurrency(request.getCurrency());
        if (currencyName == null) {
            log.warn(MessageFormat.format(LOG_INVALID_CURRENCY, request.getCurrency()));
            throw new IllegalArgumentException("Invalid currency");
        }

        // Get the authenticated user
        UserInfo userInfo =
                (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Transaction transaction = prepareDebitTransaction(request, userInfo, currencyName);

        transactionDAO.save(transaction);
        log.info(MessageFormat.format(LOG_TRANSACTION_SAVED_SUCCESSFULLY, userInfo.getUserId()));

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

        if (targetCurrency != null) {
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
                MessageFormat.format(
                        LOG_RETRIEVED_TRANSACTIONS,
                        transactionDetailsList.size(),
                        userInfo.getUserId()));

        TransactionDetailsResponse transactionDetailsResponse = new TransactionDetailsResponse();
        transactionDetailsResponse.setTransactionDetails(transactionDetailsList);

        return transactionDetailsResponse;
    }

    private TransactionDetails getTransactionDetails(
            String targetCurrency, Transaction transaction, Map<String, Double> exchangeRates) {

        Bill bill =
                (transaction.getTransactionType() == TransactionType.CREDIT)
                        ? billService.getBill(transaction.getBillId())
                        : null;

        CurrencyConversionRequest ccRequest =
                getCurrencyConversionRequest(targetCurrency, transaction, exchangeRates);

        if (targetCurrency != null) {

            if (bill != null) {
                bill.getPurchasedProducts()
                        .forEach(
                                product -> {
                                    product.setPrice(
                                            getConvertedAmount(product.getPrice(), ccRequest));
                                });
                bill.setTotalAmount(getConvertedAmount(bill.getTotalAmount(), ccRequest));
            }

            transaction.setAmount(getConvertedAmount(transaction.getAmount(), ccRequest));

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
        log.info(MessageFormat.format(LOG_FETCHING_TRANSACTION_BY_ID, id, userInfo.getUserId()));

        Map<String, Double> exchangeRates = fxRatesService.getLatestFxRates().getRates();

        Transaction transaction =
                transactionDAO
                        .findByTransactionIdAndUserId(id, userInfo.getUsername())
                        .orElse(null);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction not found");
        }

        CurrencyConversionRequest ccRequest =
                getCurrencyConversionRequest(targetCurrency, transaction, exchangeRates);

        transaction.setAmount(getConvertedAmount(transaction.getAmount(), ccRequest));
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

        TransactionDetails transactionDetails =
                transactionDAO
                        .findByTransactionIdAndUserId(id, userInfo.getUserId())
                        .map(
                                transaction -> {
                                    return getDetails(targetCurrency, transaction, exchangeRates);
                                })
                        .orElse(null);

        if (transactionDetails == null) {
            log.warn(MessageFormat.format(LOG_TRANSACTION_NOT_FOUND, id, userInfo.getUserId()));

            throw new ResourceNotFoundException("Transaction not found");
        }

        return transactionDetails;
    }

    /**
     * delete transaction by id
     *
     * @param id
     */
    @Override
    public void deleteTransaction(UUID id) {
        log.info(MessageFormat.format(LOG_DELETE_TRANSACTION_REQUEST, id));

        if (!transactionDAO.existsById(id)) {
            log.warn(MessageFormat.format(LOG_TRANSACTION_NOT_FOUND, id, "N/A"));
            throw new IllegalArgumentException("Transaction not found");
        }

        transactionDAO.deleteById(id);
        log.info(MessageFormat.format(LOG_TRANSACTION_DELETED_SUCCESSFULLY, id));
    }

    /**
     * fetch the transaction by time interval
     *
     * @param interval
     * @return
     */
    @Override
    public List<TransactionDto> fetchTransactionsByInterval(Interval interval) {
        log.info(MessageFormat.format(LOG_FETCHING_TRANSACTIONS_FOR_INTERVAL, interval));

        Date fromDate = getStartDate(interval);
        List<Transaction> transactions = transactionDAO.findByTransactionTimeAfter(fromDate);

        log.info(
                MessageFormat.format(
                        LOG_RETRIEVED_TRANSACTIONS_FOR_INTERVAL, transactions.size(), interval));
        return transactions.stream().map(TransactionUtils::mapToDTO).collect(Collectors.toList());
    }

    /**
     * fetch the bill from billService converts the currency to target currency
     *
     */
    private TransactionDetails getDetails(
            String targetCurrency, Transaction transaction, Map<String, Double> exchangeRates) {

        CurrencyConversionRequest currencyConversionRequest =
                getCurrencyConversionRequest(targetCurrency, transaction, exchangeRates);

        Bill bill = billService.getBill(transaction.getBillId());

        bill.getPurchasedProducts()
                .forEach(
                        product -> {
                            product.setPrice(
                                    getConvertedAmount(
                                            product.getPrice(), currencyConversionRequest));
                        });

        bill.setTotalAmount(getConvertedAmount(bill.getTotalAmount(), currencyConversionRequest));

        transaction.setAmount(
                getConvertedAmount(transaction.getAmount(), currencyConversionRequest));
        return mapToTransactionDetails(transaction, bill);
    }
}
