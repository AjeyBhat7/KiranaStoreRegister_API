package com.jar.kiranaregister.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RefundDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

//    CREATE TABLE refund_details (
//            refund_id VARCHAR(255) PRIMARY KEY,
//    transaction_id VARCHAR(255) NOT NULL UNIQUE,
//    refunded_bill_id INT NOT NULL,
//    refund_date TIMESTAMP NOT NULL,
//    refund_amount DECIMAL(10, 2) NOT NULL,
//    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
//    FOREIGN KEY (refunded_bill_id) REFERENCES bills(bill_id)
//            );
}
