package com.jar.kiranaregister.model.responseModel;

import java.util.Map;
import lombok.Data;

@Data
public class FxRatesResponse {
    private boolean success;
    private long timestamp;
    private String base;
    private String date;
    private Map<String, Double> rates;
}
