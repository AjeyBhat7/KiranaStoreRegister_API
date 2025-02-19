package com.jar.kiranaregister.kafka;

import static com.jar.kiranaregister.utils.ValidationUtils.validateCurrency;

import com.jar.kiranaregister.feature_report.model.requestObj.ReportRequest;
import com.jar.kiranaregister.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ReportKafkaProducer {

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
        validateCurrency(reportRequest.getCurrency());

        String interval = reportRequest.getInterval();

        if(interval == null) {
            throw new IllegalArgumentException("Interval cannot be null");
        }

        String message = StringUtils.toJson(reportRequest);
        log.info("Sending report request to Kafka - Topic: {}, Message: {}", reportTopic, message);

        kafkaTemplate.send(reportTopic, message);

        log.info("Report request successfully sent to Kafka.");
    }
}
