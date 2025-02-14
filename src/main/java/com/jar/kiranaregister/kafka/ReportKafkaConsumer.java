package com.jar.kiranaregister.kafka;

import com.jar.kiranaregister.model.requestObj.ReportRequest;
import com.jar.kiranaregister.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jar.kiranaregister.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReportKafkaConsumer {

    private final ReportService reportService;

    @Value("${kafka.topic.report}")
    private String reportTopic;

    public ReportKafkaConsumer(ReportService reportService) {
        this.reportService = reportService;

    }

    @KafkaListener(topics = "${kafka.topic.report}", groupId = "report-group")
    public void processReport(String message) {
        try {
            System.out.println(message);
            ReportRequest request = StringUtils.fromJson(message, ReportRequest.class);
            reportService.generateReport(request.getInterval(), request.getCurrency());

        } catch (Exception e) {
            System.out.println(message + ": " + e.getMessage());
//            logger.error();
        }
    }
}
