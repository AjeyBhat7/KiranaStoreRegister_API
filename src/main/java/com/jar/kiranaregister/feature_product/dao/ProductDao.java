package com.jar.kiranaregister.feature_product.dao;

import com.jar.kiranaregister.feature_product.model.entity.Product;
import com.jar.kiranaregister.feature_product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductDao {

    private final ProductRepository productRepository;

    @Autowired
    public ProductDao(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductById(String productId) {

        return productRepository.findById(productId);
    }


    public String saveProduct(Product product){

        productRepository.save(product);

        return "Saved";
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
