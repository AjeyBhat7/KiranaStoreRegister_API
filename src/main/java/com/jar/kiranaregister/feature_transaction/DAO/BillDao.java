package com.jar.kiranaregister.feature_transaction.DAO;

import com.jar.kiranaregister.feature_transaction.model.entity.Bill;
import com.jar.kiranaregister.feature_transaction.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class BillDao {

    private final BillRepository billRepository;


    @Autowired
    public BillDao(BillRepository billRepository) {
        this.billRepository = billRepository;
    }


    public Bill saveBill(Bill bill) {

        return billRepository.save(bill);
    }

    public Optional<Bill> getBillById(String billId) {

        return billRepository.findById(billId);
    }
}
