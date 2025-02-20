package com.jar.kiranaregister.feature_transaction.service.serviceImplementation;

import static com.jar.kiranaregister.feature_transaction.constants.TransactionConstants.LOG_BILL_GENERATED_SUCCESSFULLY;

import com.jar.kiranaregister.feature_transaction.dao.BillDao;
import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.model.requestObj.GenerateBillRequest;
import com.jar.kiranaregister.feature_transaction.model.responseObj.BillResponse;
import com.jar.kiranaregister.feature_transaction.service.BillService;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BillServiceImplementation implements BillService {

    private final BillDao billDao;

    @Autowired
    public BillServiceImplementation(BillDao billDao) {
        this.billDao = billDao;
    }

    /**
     * generates bill for purchased products
     *
     * @param request GenerateBillRequest
     * @return
     */
    @Override
    public BillResponse generateBillId(GenerateBillRequest request) {
        Bill bill = new Bill();
        bill.setTotalAmount(request.getAmount());
        bill.setPurchasedProducts(request.getPurchasedProductsList());
        String billId = billDao.saveBill(bill).getId();
        BillResponse billResponse = new BillResponse();
        billResponse.setBillId(billId);
        log.info(
                MessageFormat.format(
                        LOG_BILL_GENERATED_SUCCESSFULLY,
                        bill.getTotalAmount(),
                        bill.getPurchasedProducts()));
        return billResponse;
    }

    @Override
    public Bill getBill(String billId) {
        return billDao.getBillById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));
    }
}
