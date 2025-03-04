package com.jar.kiranaregister.feature_transaction.repository;

import com.jar.kiranaregister.feature_transaction.model.entity.Transaction;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Fetch transactions that happened after a certain date
    List<Transaction> findByTransactionTimeAfter(Date fromDate);

    List<Transaction> findByUserId(String username);

    Optional<Transaction> findByTransactionIdAndUserId(UUID transactionId, String userId);
}
