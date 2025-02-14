package com.jar.kiranaregister.feature_transaction.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class StringUtils {
    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            ObjectMapper ob = new ObjectMapper();
            return (T)ob.readValue(json, cls);
        } catch (Exception e) {

            return null;
        }
    }

    public static String toJson(Object object) {
        try {
            ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }
}
