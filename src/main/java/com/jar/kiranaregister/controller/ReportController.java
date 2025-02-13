package com.jar.kiranaregister.controller;

import com.jar.kiranaregister.kafka.ReportKafkaProducer;
import com.jar.kiranaregister.model.DTOModel.ReportDTO;
import com.jar.kiranaregister.model.requestObj.ReportRequest;
import com.jar.kiranaregister.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("report")
public class ReportController {

    private final ReportService reportService;
    private final ReportKafkaProducer kafkaProducer;

    public ReportController(ReportService reportService, ReportKafkaProducer kafkaProducer) {
        this.reportService = reportService;
        this.kafkaProducer = kafkaProducer;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("generate")
    public ResponseEntity<String> asyncGenerateReport(@RequestParam String interval, @RequestParam String currency) {
        ReportRequest request = new ReportRequest(interval, currency);
        kafkaProducer.sendReportRequest(request);
        return new ResponseEntity<>("Report generation started. Fetch the report later.", HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("fetch")
    public ResponseEntity<?> fetchReport(@RequestParam String interval, @RequestParam String currency) {
        ReportDTO report = reportService.fetchReport(interval, currency);
        if (report == null) {
            return new ResponseEntity<>("Report not available yet. Try again later.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(report, HttpStatus.OK);
    }
}
