package com.klasha.assessment.exception;

import lombok.Data;
import org.json.JSONObject;

@Data
public class ErrorRes {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;

    @Override
    public String toString() {
     return new JSONObject(this).toString();
    }
}
