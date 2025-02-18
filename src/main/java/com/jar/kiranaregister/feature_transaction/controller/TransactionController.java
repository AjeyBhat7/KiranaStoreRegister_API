package com.jar.kiranaregister.feature_transaction.controller;

import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetailsResponse;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionStatusResponse;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import java.util.UUID;

import com.jar.kiranaregister.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Creates a new credit transaction.
     *
     * @param request The transaction request object.
     * @return The transaction status or an error message.
     */
    @PostMapping("credit")
    public ResponseEntity<ApiResponse> createCreditTransaction(@RequestBody TransactionRequest request) {
        log.info("Received request to create credit transaction: {}", request);

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        TransactionStatusResponse status = transactionService.addTransaction(request);
        response.setData(status);

        log.info("Credit transaction created successfully: {}", status);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Creates a new debit transaction.
     *
     * @param request The transaction request object.
     * @return The transaction status or an error message.
     */
    @PostMapping("debit")
    public ResponseEntity<ApiResponse> createDebitTransaction(@RequestBody DebitTransactionRequest request) {
        log.info("Received request to create debit transaction: {}", request);



        TransactionStatus status = transactionService.debitTransaction(request);
        log.info("Debit transaction created successfully: {}", status);

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setData(status);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /** Retrieves all transactions by currency. */
    @GetMapping("getAll")
    public ResponseEntity<ApiResponse> getAllTransactions(@RequestParam(required = false) String currency) {
        log.info("Fetching all transactions with currency : {}", currency);

        TransactionDetailsResponse transactions = transactionService.getAllTransactions(currency);
        log.info("Successfully retrieved all transactions");

        ApiResponse response = new ApiResponse();
        response.setData(transactions);
        response.setSuccess(true);
        response.setStatus(HttpStatus.OK.name());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a specific transaction by ID.
     *
     * @return The transaction details or an error message.
     */
    @GetMapping("getTransactionById")
    public ResponseEntity<ApiResponse> getTransactionById(
            @RequestParam UUID id, @RequestParam(required = false) String currency) {

        TransactionDTO transaction = transactionService.getTransactionById(id, currency);

        log.info("Successfully retrieved transactionId: {}", transaction.getId());
        ApiResponse response = new ApiResponse();
        response.setData(transaction);
        response.setSuccess(true);
        response.setStatus(HttpStatus.OK.name());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** Delete transaction by ID. */
    @DeleteMapping("deleteTransaction")
    public ResponseEntity<ApiResponse> deleteTransaction(@RequestParam UUID id) {
        log.info("Received request to delete transaction with ID: {}", id);

        transactionService.deleteTransaction(id);
        log.info("Transaction deleted successfully: {}", id);
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setDisplayMsg("Transaction deleted successfully");
        response.setStatus(HttpStatus.OK.name());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
