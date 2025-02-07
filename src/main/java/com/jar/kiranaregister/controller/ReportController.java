package com.jar.kiranaregister.controller;


import com.jar.kiranaregister.enums.Currency;
import com.jar.kiranaregister.enums.Interval;
import com.jar.kiranaregister.model.DTOModel.ReportDTO;
import com.jar.kiranaregister.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("fetchReport")
    public ResponseEntity<?> fetchReport(@RequestParam String interval, @RequestParam(required = false) String currency) {

        ReportDTO report =  reportService.fetchReport(interval, currency);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @PostMapping("GenerateReport")
    public ResponseEntity<?> generateReport(@RequestParam String interval) {

        reportService.generateReport(interval);

        return new ResponseEntity<>("Report is being generated, please wait", HttpStatus.OK);
    }
}
