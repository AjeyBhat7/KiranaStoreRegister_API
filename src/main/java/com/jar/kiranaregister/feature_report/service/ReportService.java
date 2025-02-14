package com.jar.kiranaregister.feature_report.service;

import com.jar.kiranaregister.feature_report.model.dto.ReportDTO;

public interface ReportService {

    public ReportDTO generateReport(String interval, String currency);

    public ReportDTO fetchReport(String interval, String currency);
}
