package com.jar.kiranaregister.feature_product.model.responseObj;

import com.jar.kiranaregister.feature_product.model.entity.Product;
import java.util.List;
import lombok.Data;

@Data
public class ProductListRes {
    private List<Product> products;
}
