package com.jar.kiranaregister.feature_product.service;

import com.jar.kiranaregister.feature_product.model.dto.ProductDto;
import com.jar.kiranaregister.feature_product.model.entity.Product;
import java.util.List;

public interface ProductService {


    /**
     * create new product ;
     * @param product
     * @return
     */
    public String create(Product product);

    /**
     * fetch product details by id
     * @param productId
     * @return
     */
    public ProductDto getProductById(String productId);

    /**
     * returns all products .
     * @return
     */
    public List<Product> getAllProducts();
}
