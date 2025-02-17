package com.jar.kiranaregister.feature_transaction.model.entity;

import com.jar.kiranaregister.feature_transaction.model.DTOModel.PurchasedProductsDetails;
import jakarta.persistence.Id;
import java.util.List;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "bills")
public class Bill {

    @Id private String id;

    private List<PurchasedProductsDetails> purchasedProducts;

    private double totalAmount;
}
