package com.jar.kiranaregister.kafka;

import com.jar.kiranaregister.feature_transaction.model.requestObj.ReportRequest;
import com.jar.kiranaregister.feature_report.service.ReportService;
import com.jar.kiranaregister.feature_transaction.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReportKafkaConsumer {

    private final ReportService reportService;

    @Value("${kafka.topic.report}")
    private String reportTopic;

    public ReportKafkaConsumer(ReportService reportService) {
        this.reportService = reportService;

    }

    /**
     * consumes kafka message and and calls generate report function
     *
     * @param message
     */

    @KafkaListener(topics = "${kafka.topic.report}", groupId = "report-group")
    public void processReport(String message) {
        try {
            log.info("consuming message from broker: {}, Message: ", message);

            ReportRequest request = StringUtils.fromJson(message, ReportRequest.class);
            reportService.generateReport(request.getInterval(), request.getCurrency());



        } catch (Exception e) {
            log.error(message + ": " + e.getMessage());
        }
    }
}
