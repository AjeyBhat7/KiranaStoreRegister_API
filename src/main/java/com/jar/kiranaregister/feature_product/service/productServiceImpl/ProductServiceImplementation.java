package com.jar.kiranaregister.feature_product.service.productServiceImpl;

import com.jar.kiranaregister.feature_product.dao.ProductDao;
import com.jar.kiranaregister.feature_product.model.dto.ProductDto;
import com.jar.kiranaregister.feature_product.model.entity.Product;
import com.jar.kiranaregister.feature_product.model.responseObj.ProductListRes;
import com.jar.kiranaregister.feature_product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImplementation implements ProductService {

    private final ProductDao productDao;

    @Autowired
    public ProductServiceImplementation(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public String create(Product product) {
        return productDao.saveProduct(product);
    }

    @Override
    public ProductDto getProductById(String productId) {
        Product product =  productDao.getProductById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        return getProductDto(product);
    }

    @Override
    public List<Product> getAllProducts() {

        List<Product> products =  productDao.getAllProducts();

        ProductListRes productListRes = new ProductListRes();
        productListRes.setProducts(products);

        return products;
    }


//    convert to dto

    private ProductDto getProductDto(Product product){
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        return productDto;
    }
}
