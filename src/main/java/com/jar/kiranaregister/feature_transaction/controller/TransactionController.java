package com.jar.kiranaregister.feature_transaction.controller;

import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetails;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.TransactionDetailsResponse;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import java.util.List;
import java.util.UUID;

import com.jar.kiranaregister.feature_users.model.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<?> createCreditTransaction(@RequestBody TransactionRequest request) {
        log.info("Received request to create credit transaction: {}", request);

        TransactionStatus status =
                    transactionService.addTransaction(request);
        log.info("Credit transaction created successfully: {}", status);

        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    /**
     * Creates a new debit transaction.
     *
     * @param request The transaction request object.
     * @return The transaction status or an error message.
     */
    @PostMapping("debit")
    public ResponseEntity<?> createDebitTransaction(@RequestBody DebitTransactionRequest request) {
        log.info("Received request to create debit transaction: {}", request);

            TransactionStatus status =
                    transactionService.debitTransaction(request);
            log.info("Debit transaction created successfully: {}", status);

            return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    /**
     * Retrieves all transactions by currency.
     *

     */
    @GetMapping("getAll")
    public ResponseEntity<?> getAllTransactions(@RequestParam(required = false) String currency) {
        log.info("Fetching all transactions with currency filter: {}", currency);

            TransactionDetailsResponse transactions = transactionService.getAllTransactions(currency);
            log.info("Successfully retrieved all transactions");
            return new ResponseEntity<>(transactions, HttpStatus.OK);

    }

    /**
     * Retrieves a specific transaction by ID.
     *
     * @return The transaction details or an error message.
     */
    @GetMapping("getTransactionById")
    public ResponseEntity<?> getTransactionById(
            @RequestParam UUID id, @RequestParam(required = false) String currency) {

            // Get user details from the security context
            UserInfo userDetails =
                    (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            TransactionDTO transaction = transactionService.getTransactionById(id, currency);

            log.info("Successfully retrieved transactionId: {}", transaction.getId());

            return new ResponseEntity<>(transaction, HttpStatus.OK);

    }

    /**
     * Delete transaction by ID.
     *
     */
    @DeleteMapping("deleteTransaction")
    public ResponseEntity<?> deleteTransaction(@RequestParam UUID id) {
        log.info("Received request to delete transaction with ID: {}", id);

        transactionService.deleteTransaction(id);
        log.info("Transaction deleted successfully: {}", id);
        return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);

    }
}
