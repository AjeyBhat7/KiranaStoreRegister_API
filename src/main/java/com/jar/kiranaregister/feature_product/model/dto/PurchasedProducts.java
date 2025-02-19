package com.jar.kiranaregister.feature_product.model.dto;

import lombok.Data;

@Data
public class PurchasedProducts {
    String productId;
    long quantity;
    double price;
}
