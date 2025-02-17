package com.jar.kiranaregister.feature_transaction.service.serviceImplementation;

import com.jar.kiranaregister.feature_transaction.DAO.BillDao;
import com.jar.kiranaregister.feature_product.model.dto.ProductDto;
import com.jar.kiranaregister.feature_transaction.model.DTOModel.PurchasedProductsDetails;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_product.model.dto.PurchasedProducts;
import com.jar.kiranaregister.feature_transaction.service.BillService;
import com.jar.kiranaregister.feature_product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BillServiceImpl implements BillService {

    private final ProductService productService;
    private final BillDao billDao;

    @Autowired
    public BillServiceImpl(ProductService productService, BillDao billDao) {
        this.productService = productService;
        this.billDao = billDao;
    }

    @Override
    public String generateBillId(List<PurchasedProducts> purchasedProducts) {
        // fetch price from product service and save it
        List<PurchasedProductsDetails> purchasedProductsDetails = purchasedProducts.stream()
                .map(product -> {
                    ProductDto productDto = productService.getProductById(product.getProductId());

                    PurchasedProductsDetails details = new PurchasedProductsDetails();
                    details.setProductId(product.getProductId());
                    details.setQuantity(product.getQuantity());
                    details.setPrice(productDto.getPrice());

                    return details;
                })
                .toList();

        double totalAmount = purchasedProductsDetails.stream()
                .map(product -> product.getPrice() * product.getQuantity())
                .reduce(0.0, Double::sum);

        // Create a new Bill
        Bill bill = new Bill();
        bill.setTotalAmount(totalAmount);
        bill.setPurchasedProducts(purchasedProductsDetails);

        Bill newBill = billDao.saveBill(bill);

        return newBill.getId();
    }

    @Override
    public Bill getBill(String billId) {
        return billDao.getBillById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));
    }
}
