package com.jar.kiranaregister.feature_transaction.dao;

import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.repository.BillRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillDao {

    private final BillRepository billRepository;

    @Autowired
    public BillDao(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    /**
     * save the bill in db
     *
     * @param bill
     * @return
     */
    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }

    /**
     * fetch the bill by id
     *
     * @param billId
     * @return
     */
    public Optional<Bill> getBillById(String billId) {
        return billRepository.findById(billId);
    }
}
