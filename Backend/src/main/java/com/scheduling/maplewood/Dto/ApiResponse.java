package com.scheduling.maplewood.Dto;

import lombok.Data;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ApiResponse {

    /**
     * Returns a map with a success flag and the result data.
     * 
     * @param data the result data
     * @return a map with a success flag and the result data
     */
    public static Map<String, Object> success(Object data) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("result", data);
        return res;
    }

    /**
     * Returns a map with a success flag and the result data.
     * @param message the error message
     * @param details the error details
     * @return a map with a success flag and the result data
     */
    public static Map<String, Object> error(String message, String details) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", false);
        res.put("message", message);
        res.put("error", details);
        return res;
    }
}

