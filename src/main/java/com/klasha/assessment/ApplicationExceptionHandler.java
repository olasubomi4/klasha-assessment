package com.klasha.assessment;

import com.klasha.assessment.exception.EntityNotFoundException;
import com.klasha.assessment.exception.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class ApplicationExceptionHandler  extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(@NotNull RuntimeException ex) {
        // You can customize the response here
        List<String> errors = new ArrayList<>();

        errors.add(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Request Could not be processed at the moment",errors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {

    List<String> errors = new ArrayList<>();

    errors.add(ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse("Not found",errors);

    return ResponseEntity.badRequest().body(errorResponse);}
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {

    List<String> errors = new ArrayList<>();

    errors.add("Invalid parameter value: " + ex.getName()+" "+ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse("Invalid data type Error",errors);

    return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler( {ConstraintViolationException.class})
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();

        ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));
        ErrorResponse errorResponse = new ErrorResponse("Validation Error",errors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String paramName = ex.getParameterName();
        List<String> errors = new ArrayList<>();
        errors.add("Required parameter '" + paramName + "' is missing.");
        ErrorResponse errorResponse = new ErrorResponse("Missing parameter Error",errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // Get the validation errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse("Validation Error",errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(RuntimeException ex) {

        ErrorResponse errorResponse = new ErrorResponse("Resource not found",Arrays.asList(ex.getMessage()));
        return ResponseEntity.badRequest().body(errorResponse);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Data Integrity Violation: we cannot process your request",Arrays.asList(ex.getMessage()));
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
