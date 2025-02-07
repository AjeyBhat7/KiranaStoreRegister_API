package com.jar.kiranaregister.repository;

import com.jar.kiranaregister.model.DTOModel.ProductDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<ProductDTO, Integer> {}
