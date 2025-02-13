package com.jar.kiranaregister.kafka;

import com.jar.kiranaregister.model.requestObj.ReportRequest;
import com.jar.kiranaregister.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportKafkaProducer {

    @Value("${kafka.topic.report}")
    private String reportTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ReportKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReportRequest(ReportRequest reportRequest) {
        try {
            String message = StringUtils.toJson(reportRequest);
            kafkaTemplate.send(reportTopic, message);
        } catch (Exception e) {
            // Log error for debugging
            System.err.println("Error sending Kafka message: " + e.getMessage());
        }
    }
}
