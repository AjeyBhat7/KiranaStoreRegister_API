package com.jar.kiranaregister.feature_product.service;

import com.jar.kiranaregister.feature_product.model.dto.ProductDto;
import com.jar.kiranaregister.feature_product.model.entity.Product;
import java.util.List;

public interface ProductService {

    public String create(Product product);

    public ProductDto getProductById(String productId);

    public List<Product> getAllProducts();
}
