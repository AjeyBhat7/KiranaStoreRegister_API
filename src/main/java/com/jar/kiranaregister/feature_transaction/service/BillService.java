package com.jar.kiranaregister.feature_transaction.service;

import com.jar.kiranaregister.feature_product.model.dto.PurchasedProducts;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.responseObj.BillResponse;
import java.util.List;

public interface BillService {

    public BillResponse generateBillId(List<PurchasedProducts> purchasedProducts);

    public Bill getBill(String billId);
}
