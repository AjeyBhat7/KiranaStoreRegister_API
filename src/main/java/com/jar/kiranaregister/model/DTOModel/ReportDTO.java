package com.jar.kiranaregister.model.DTOModel;

import lombok.Data;

import java.util.List;


@Data
public class ReportDTO {

    double credits;
    double debits;

    double netFlow;

    List<TransactionDTO> transactions;

}
