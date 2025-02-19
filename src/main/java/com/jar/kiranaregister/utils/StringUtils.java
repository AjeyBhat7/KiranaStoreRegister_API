package com.jar.kiranaregister.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class StringUtils {
    /**
     * converts string to requested object
     * @param json
     * @param cls
     * @return
     * @param <T>
     */
    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            ObjectMapper ob = new ObjectMapper();
            return (T) ob.readValue(json, cls);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * converts object to string
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        try {
            ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }
}
