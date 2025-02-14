package com.jar.kiranaregister.feature_transaction.model.responseObj;


import com.jar.kiranaregister.feature_product.model.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductListRes {
    private List<Product> products;
}
