package com.jar.kiranaregister.feature_transaction.controller;


import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDto;
import com.jar.kiranaregister.feature_transaction.model.requestObj.CreditTransactionRequest;
import com.jar.kiranaregister.feature_transaction.model.requestObj.DebitTransactionRequest;
import com.jar.kiranaregister.feature_transaction.service.TransactionService;
import com.jar.kiranaregister.response.ApiResponse;
import java.util.UUID;
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
    public ResponseEntity<ApiResponse> createCreditTransaction(
            @RequestBody CreditTransactionRequest request) {

        ApiResponse response = new ApiResponse();
        response.setData(transactionService.addTransaction(request));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Creates a new debit transaction.
     *
     * @param request The transaction request object.
     * @return The transaction status or an error message.
     */
    @PostMapping("debit")
    public ResponseEntity<ApiResponse> createDebitTransaction(
            @RequestBody DebitTransactionRequest request) {

        ApiResponse response = new ApiResponse();
        response.setData(transactionService.debitTransaction(request));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /** Retrieves all transactions by currency. */
    @GetMapping("getAll")
    public ResponseEntity<ApiResponse> getAllTransactions(
            @RequestParam(required = false) String currency) {

//        TransactionDetailsResponse transactions = transactionService.getAllTransactions(currency);

        ApiResponse response = new ApiResponse();
        response.setData(transactionService.getAllTransactions(currency));

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

        TransactionDto transaction = transactionService.getTransactionById(id, currency);

        ApiResponse response = new ApiResponse();
        response.setData(transaction);
        response.setStatus(HttpStatus.OK.name());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** Delete transaction by ID. */
    @DeleteMapping("deleteTransaction")
    public ResponseEntity<ApiResponse> deleteTransaction(@RequestParam UUID id) {

        transactionService.deleteTransaction(id);

        ApiResponse response = new ApiResponse();
        response.setDisplayMsg("Transaction deleted successfully");
        response.setStatus(HttpStatus.OK.name());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
