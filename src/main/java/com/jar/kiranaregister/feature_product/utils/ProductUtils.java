package com.jar.kiranaregister.feature_product.utils;

import com.jar.kiranaregister.feature_product.model.dto.ProductDto;
import com.jar.kiranaregister.feature_product.model.entity.Product;

public class ProductUtils {

    public static ProductDto getProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        return productDto;
    }
}
