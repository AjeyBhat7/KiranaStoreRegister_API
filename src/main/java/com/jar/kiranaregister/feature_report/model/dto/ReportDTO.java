package com.jar.kiranaregister.feature_report.model.dto;

import java.util.List;

import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import lombok.Data;

@Data
public class ReportDTO {

    double credits;
    double debits;

    double netFlow;

    List<TransactionDTO> transactions;
}
