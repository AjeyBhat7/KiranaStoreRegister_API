package com.jar.kiranaregister.feature_report.model.dto;

import com.jar.kiranaregister.feature_transaction.model.DTOModel.TransactionDTO;
import java.util.List;
import lombok.Data;

@Data
public class ReportDTO {

    double credits;
    double debits;

    double netFlow;

    List<TransactionDTO> transactions;
}
