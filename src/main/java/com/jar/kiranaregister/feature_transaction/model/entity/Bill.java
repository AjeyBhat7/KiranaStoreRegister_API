package com.jar.kiranaregister.feature_transaction.model.entity;

import com.jar.kiranaregister.feature_product.model.dto.PurchasedProducts;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.PurchasedProductsDetails;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "bills")
public class Bill {

    @Id
    private String billId;

    private List<PurchasedProductsDetails> purchasedProducts;

    private double totalAmount;
}
