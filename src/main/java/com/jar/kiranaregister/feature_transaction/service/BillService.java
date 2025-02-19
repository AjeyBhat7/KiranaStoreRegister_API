package com.jar.kiranaregister.feature_transaction.service;

import com.jar.kiranaregister.feature_product.model.dto.PurchasedProducts;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.requestObj.GenerateBillRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.BillResponse;
import java.util.List;

public interface BillService {

    /**
     * fetch the list of transaction and generates the bill
     * @param request GenerateBillRequest
     * @return
     */
    BillResponse generateBillId(GenerateBillRequest request);

    /**
     * fetch the bill by id
     * @param billId
     * @return
     */
    Bill getBill(String billId);
}
