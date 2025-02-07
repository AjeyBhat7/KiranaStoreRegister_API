package com.jar.kiranaregister.model.requestObj;

import com.jar.kiranaregister.enums.Currency;
import com.jar.kiranaregister.enums.TransactionType;
import com.jar.kiranaregister.model.DTOModel.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private double amount;
    private String currency;

}