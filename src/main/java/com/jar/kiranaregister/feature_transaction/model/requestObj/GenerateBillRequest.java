package com.jar.kiranaregister.feature_transaction.model.requestObj;

import com.jar.kiranaregister.feature_product.model.dto.PurchasedProducts;
import java.util.List;
import lombok.Data;

@Data
public class GenerateBillRequest {
    List<PurchasedProducts> purchasedProductsList;
    double amount;
}
