package com.jar.kiranaregister.feature_transaction.model.requestObj;

import com.jar.kiranaregister.feature_product.model.dto.PurchasedProducts;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private double amount;
    private String currency;

    List<PurchasedProducts> purchasedProducts;
}
