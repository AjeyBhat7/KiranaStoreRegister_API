package com.jar.kiranaregister.kafka;

import com.jar.kiranaregister.feature_report.model.requestObj.ReportRequest;
import com.jar.kiranaregister.feature_report.service.ReportService;
import com.jar.kiranaregister.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ReportKafkaConsumer {

    private final ReportService reportService;

    @Autowired
    public ReportKafkaConsumer(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * consumes kafka message and calls generate report function
     *
     * @param message
     */
    @KafkaListener(topics = "${kafka.topic.report}", groupId = "report-group")
    public void processReport(String message) {
        try {
            log.info("consuming message from broker: {}, Message: ", message);

            ReportRequest request = StringUtils.fromJson(message, ReportRequest.class);
            String interval = Optional.ofNullable(request).map(ReportRequest::getInterval).orElse(null);
            String currency = Optional.ofNullable(request).map(ReportRequest::getCurrency).orElse(null);


            reportService.generateReport(interval, currency);

        } catch (Exception e) {
            log.error(message + ": " + e.getMessage());
        }
    }
}
