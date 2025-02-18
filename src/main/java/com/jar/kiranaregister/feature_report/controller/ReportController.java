package com.jar.kiranaregister.feature_report.controller;

import com.jar.kiranaregister.feature_report.model.dto.ReportDTO;
import com.jar.kiranaregister.feature_report.model.requestObj.ReportRequest;
import com.jar.kiranaregister.feature_report.service.ReportService;
import com.jar.kiranaregister.kafka.ReportKafkaProducer;
import com.jar.kiranaregister.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("report")
public class ReportController {
    
    private final ReportService reportService;
    private final ReportKafkaProducer kafkaProducer;

    public ReportController(ReportService reportService, ReportKafkaProducer kafkaProducer) {
        this.reportService = reportService;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * async report generation via Kafka. users with 'ADMIN' authority can accesses.
     *
     * @param interval
     * @param currency
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("generate")
    public ResponseEntity<ApiResponse> asyncGenerateReport(
            @RequestParam String interval, @RequestParam String currency) {
        log.info(
                "generating report for - Interval: {}, Currency: {}",
                interval,
                currency);

        ReportRequest request = new ReportRequest(interval, currency);
        kafkaProducer.sendReportRequest(request);

        log.info("Report generation request sent to Kafka.");

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setStatus(HttpStatus.ACCEPTED.name());
        response.setDisplayMsg("Report generation request sent to Kafka.");

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    /**
     * Fetches a generated report based on the given interval and currency. Only users with 'ADMIN'
     * authority can access this report.
     *
     * @param interval
     * @param currency
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("fetch")
    public ResponseEntity<ApiResponse> fetchReport(
            @RequestParam String interval, @RequestParam String currency) {
        log.info("Fetching report - Interval: {}, Currency: {}", interval, currency);

        ReportDTO report = reportService.fetchReport(interval, currency);

        log.info("Report successfully fetched.");

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setStatus(HttpStatus.OK.name());
        response.setData(report);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
