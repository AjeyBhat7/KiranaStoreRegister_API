package com.jar.kiranaregister.feature_transaction.model.DTOModel;

import com.jar.kiranaregister.enums.CurrencyName;
import com.jar.kiranaregister.enums.TransactionStatus;
import com.jar.kiranaregister.enums.TransactionType;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jar.kiranaregister.feature_product.model.dto.PurchasedProducts;
import com.jar.kiranaregister.feature_product.model.entity.Product;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TransactionDTO {

    private UUID id;
    private TransactionStatus status;
    private Date transactionTime;
    private Double amount;
    private CurrencyName currencyName;
    private TransactionType transactionType;
}
