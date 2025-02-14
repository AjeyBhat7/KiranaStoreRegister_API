package com.jar.kiranaregister.feature_transaction.repository;

import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BillRepository extends MongoRepository<Bill, String> {
}
