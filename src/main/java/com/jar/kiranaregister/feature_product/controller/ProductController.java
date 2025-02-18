package com.jar.kiranaregister.feature_product.controller;

import com.jar.kiranaregister.feature_product.model.entity.Product;
import com.jar.kiranaregister.feature_product.service.ProductService;
import java.util.List;

import com.jar.kiranaregister.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse> addProduct(@RequestBody Product product) {
        productService.create(product);
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setDisplayMsg("product added Successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("getAllProducts")
    public ResponseEntity<ApiResponse> getAllProducts() {
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setData(productService.getAllProducts());
        response.setStatus(HttpStatus.OK.name());

        return ResponseEntity.ok(response);
    }
}
