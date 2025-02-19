package com.jar.kiranaregister.feature_product.service.serviceImpl;

import com.jar.kiranaregister.feature_product.dao.ProductDao;
import com.jar.kiranaregister.feature_product.model.dto.ProductDto;
import com.jar.kiranaregister.feature_product.model.entity.Product;
import com.jar.kiranaregister.feature_product.model.responseObj.ProductListRes;
import com.jar.kiranaregister.feature_product.service.ProductService;
import java.util.List;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jar.kiranaregister.feature_product.utils.ProductUtils.getProductDto;

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
        Product product = productDao.getProductById(productId).orElse(null);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        return getProductDto(product);
    }

    @Override
    public List<Product> getAllProducts() {

        List<Product> products = productDao.getAllProducts();

        ProductListRes productListRes = new ProductListRes();
        productListRes.setProducts(products);

        return products;
    }

}
