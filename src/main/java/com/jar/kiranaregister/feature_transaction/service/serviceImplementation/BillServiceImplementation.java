package com.jar.kiranaregister.feature_transaction.service.serviceImplementation;

import com.jar.kiranaregister.feature_product.service.ProductService;
import com.jar.kiranaregister.feature_transaction.dao.BillDao;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.requestObj.GenerateBillRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.BillResponse;
import com.jar.kiranaregister.feature_transaction.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillServiceImplementation implements BillService {

    private final ProductService productService;
    private final BillDao billDao;

    @Autowired
    public BillServiceImplementation(ProductService productService, BillDao billDao) {
        this.productService = productService;
        this.billDao = billDao;
    }

    /**
     * generates bill for purchased products
     * @param request GenerateBillRequest
     * @return
     */
    @Override
    public BillResponse generateBillId(GenerateBillRequest request) {

        // Create a new Bill
        Bill bill = new Bill();
        bill.setTotalAmount(request.getAmount());
        bill.setPurchasedProducts(request.getPurchasedProductsList());

        String billId = billDao.saveBill(bill).getId();

        BillResponse billResponse = new BillResponse();
        billResponse.setBillId(billId);

        return billResponse;
    }

    @Override
    public Bill getBill(String billId) {
        return billDao.getBillById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));
    }
}
