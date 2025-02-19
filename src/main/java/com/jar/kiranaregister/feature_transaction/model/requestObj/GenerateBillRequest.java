package com.jar.kiranaregister.feature_transaction.model.requestObj;


import com.jar.kiranaregister.feature_product.model.dto.PurchasedProducts;
import lombok.Data;

import java.util.List;

@Data
public class GenerateBillRequest {
    List<PurchasedProducts> purchasedProductsList;
    double amount;


}
