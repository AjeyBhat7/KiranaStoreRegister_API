package com.jar.kiranaregister.controller;

import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.model.DTOModel.TransactionDTO;
import com.jar.kiranaregister.model.Transaction;
import com.jar.kiranaregister.model.requestObj.TransactionRequest;
import com.jar.kiranaregister.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("credit")
    public ResponseEntity<?> createCreditTransaction(@RequestBody TransactionRequest request) {
        try {
            TransactionStatus status = transactionService.addTransaction(request, TransactionType.CREDIT);
            return new ResponseEntity<>(status, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("debit")
    public ResponseEntity<?> createDebitTransaction(@RequestBody TransactionRequest request) {
        try {
            TransactionStatus status = transactionService.addTransaction(request, TransactionType.DEBIT);

            return new ResponseEntity<>(status, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("getAll")
    public ResponseEntity<?> getAllTransactions(@RequestParam(required = false) String currency) {

        try {
            List<TransactionDTO> transactions = transactionService.getAllTransactions(currency);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("getTransactionById")
    public ResponseEntity<?> getTransactionById(@RequestParam UUID id, @RequestParam(required = false) String currency) {
        try {
            TransactionDTO transaction = transactionService.getTransactionById(id, currency);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Transaction not found", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("deleteTransaction")
    public ResponseEntity<?> deleteTransaction(@RequestParam UUID id) {
        try {
            transactionService.deleteTransaction(id);
            return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Transaction not found", HttpStatus.BAD_REQUEST);
        }
    }
}