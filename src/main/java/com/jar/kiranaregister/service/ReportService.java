package com.jar.kiranaregister.service;

import com.jar.kiranaregister.model.DTOModel.ReportDTO;

public interface ReportService {

    public ReportDTO generateReport(String interval);

    public ReportDTO fetchReport(String interval, String currency);
}
