package com.jar.kiranaregister.feature_report.controller;

import com.jar.kiranaregister.kafka.ReportKafkaProducer;
import com.jar.kiranaregister.feature_report.model.dto.ReportDTO;
import com.jar.kiranaregister.feature_report.model.requestObj.ReportRequest;
import com.jar.kiranaregister.feature_report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("report")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;
    private final ReportKafkaProducer kafkaProducer;

    public ReportController(ReportService reportService, ReportKafkaProducer kafkaProducer) {
        this.reportService = reportService;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     *  async report generation via Kafka.
     * users with 'ADMIN' authority can accesses.
     * @param interval
     * @param currency
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("generate")
    public ResponseEntity<String> asyncGenerateReport(@RequestParam String interval, @RequestParam String currency) {
        logger.info("Received request to generate report - Interval: {}, Currency: {}", interval, currency);

        ReportRequest request = new ReportRequest(interval, currency);
        kafkaProducer.sendReportRequest(request);

        logger.info("Report generation request sent to Kafka.");

        return new ResponseEntity<>("Report generation started. Fetch the report later.", HttpStatus.ACCEPTED);
    }

    /**
     *  Fetches a generated report based on the given interval and currency.
     * Only users with 'ADMIN' authority can access this report.
     * @param interval
     * @param currency
     * @return
     */

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("fetch")
    public ResponseEntity<?> fetchReport(@RequestParam String interval, @RequestParam String currency) {
        logger.info("Fetching report - Interval: {}, Currency: {}", interval, currency);

        ReportDTO report = reportService.fetchReport(interval, currency);

        if (report == null) {
            logger.warn("Report not available yet for Interval: {}, Currency: {}", interval, currency);
            return new ResponseEntity<>("Report not available yet. Try again later.", HttpStatus.NOT_FOUND);
        }

        logger.info("Report successfully fetched.");
        return new ResponseEntity<>(report, HttpStatus.OK);
    }
}
