package com.jar.kiranaregister.repository;

import com.jar.kiranaregister.model.Transaction;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Fetch transactions that happened after a certain date
    List<Transaction> findByTransactionTimeAfter(Date fromDate);
}
