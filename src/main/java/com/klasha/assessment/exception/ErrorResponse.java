package com.klasha.assessment.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private List<String> errors;
    private Exception trace;

    public ErrorResponse(String message,List<String> errors) {
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
}
