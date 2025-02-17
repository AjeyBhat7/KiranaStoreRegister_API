package com.jar.kiranaregister.feature_product.controller;

import com.jar.kiranaregister.feature_product.model.entity.Product;
import com.jar.kiranaregister.feature_product.service.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        productService.create(product);
        return ResponseEntity.ok("added");
    }

    @GetMapping("getAllProducts")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}
