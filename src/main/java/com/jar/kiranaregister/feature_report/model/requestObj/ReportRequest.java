package com.jar.kiranaregister.feature_report.model.requestObj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
    String interval;
    String currency;
}
