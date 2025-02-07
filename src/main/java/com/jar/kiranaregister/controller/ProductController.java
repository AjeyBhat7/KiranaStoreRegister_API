package com.jar.kiranaregister.controller;

import com.jar.kiranaregister.model.DTOModel.ProductDTO;
import com.jar.kiranaregister.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController()
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/allProducts")
    public ResponseEntity<?> getAllProducts(){
        List<ProductDTO> allProducts = productRepository.findAll();
        return ResponseEntity.ok(allProducts);

    }

    @PostMapping()
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO product){

        try{
            productRepository.save(product);
            return ResponseEntity.ok("added");
        } catch (Exception e){
            return (ResponseEntity<?>) ResponseEntity.internalServerError();
        }
    }

}
