package com.jar.kiranaregister.model.DTOModel;

import java.util.List;
import lombok.Data;

@Data
public class ReportDTO {

    double credits;
    double debits;

    double netFlow;

    List<TransactionDTO> transactions;
}
