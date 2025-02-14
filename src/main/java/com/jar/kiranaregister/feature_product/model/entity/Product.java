package com.jar.kiranaregister.feature_product.model.entity;


import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Product {

    @Id
    private String id;

    private String name;


    private String description;
    private Double price;

}
