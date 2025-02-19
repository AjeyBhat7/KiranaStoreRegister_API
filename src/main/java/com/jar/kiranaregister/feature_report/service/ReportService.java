package com.jar.kiranaregister.feature_report.service;

import com.jar.kiranaregister.feature_report.model.responseObj.ReportResponse;

public interface ReportService {

    void generateReport(String interval, String currency);

    ReportResponse fetchReport(String interval, String currency);
}
