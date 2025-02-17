package com.jar.kiranaregister.kafka;

import com.jar.kiranaregister.feature_report.model.requestObj.ReportRequest;
import com.jar.kiranaregister.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(ReportKafkaProducer.class);

    @Value("${kafka.topic.report}")
    private String reportTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ReportKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends report request msg to the Kafka topic.
     *
     * @param reportRequest the request containing report generation details
     */
    public void sendReportRequest(ReportRequest reportRequest) {
        try {
            String message = StringUtils.toJson(reportRequest);
            logger.info(
                    "Sending report request to Kafka - Topic: {}, Message: {}",
                    reportTopic,
                    message);

            kafkaTemplate.send(reportTopic, message);

            logger.info("Report request successfully sent to Kafka.");
        } catch (Exception e) {
            logger.error("Error sending Kafka message: {}", e.getMessage(), e);
        }
    }
}
