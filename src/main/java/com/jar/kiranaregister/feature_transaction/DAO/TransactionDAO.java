package com.jar.kiranaregister.feature_transaction.DAO;

import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import com.jar.kiranaregister.feature_transaction.repository.TransactionRepository;
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

    /**
     * saves data in db
     * @param transaction
     */

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public List<Transaction> findByUserId(String userId) {

        return transactionRepository.findByUserId(userId);
    }

    public Optional<Transaction> findByTransactionIdAndUserId(UUID id, String userId) {

        return transactionRepository.findByTransactionIdAndUserId(id, userId);
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
