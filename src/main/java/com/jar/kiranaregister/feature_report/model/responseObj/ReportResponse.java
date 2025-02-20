package com.jar.kiranaregister.feature_report.model.responseObj;

import com.jar.kiranaregister.feature_transaction.model.dto.TransactionDto;
import java.util.List;
import lombok.Data;

@Data
public class ReportResponse {
    double credits;
    double debits;
    double netFlow;
    List<TransactionDto> transactions;
    String errorMessage;
}
