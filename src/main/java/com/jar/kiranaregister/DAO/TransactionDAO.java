package com.jar.kiranaregister.DAO;

import com.jar.kiranaregister.model.entity.Transaction;
import com.jar.kiranaregister.repository.TransactionRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionDAO {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionDAO(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public List<Transaction> findByUserId(String username) {

        return transactionRepository.findByUserId(username);
    }

    public Optional<Transaction> findByTransactionIdAndUserId(UUID id, String username) {

        return transactionRepository.findByTransactionIdAndUserId(id, username);
    }

    public boolean existsById(UUID id) {
        return transactionRepository.existsById(id);
    }

    public void deleteById(UUID id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> findByTransactionTimeAfter(Date fromDate) {
        return transactionRepository.findByTransactionTimeAfter(fromDate);
    }
}
