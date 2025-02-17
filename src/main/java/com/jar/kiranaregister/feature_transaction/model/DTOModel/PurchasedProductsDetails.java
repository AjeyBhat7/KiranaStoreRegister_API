package com.jar.kiranaregister.feature_transaction.model.DTOModel;

import lombok.Data;

@Data
public class PurchasedProductsDetails {
    String productId;

    Long quantity;

    Double price;
}
