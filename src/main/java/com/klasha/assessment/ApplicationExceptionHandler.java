package com.klasha.assessment;

import com.klasha.assessment.exception.CityPopulationException;
import com.klasha.assessment.exception.ErrorResponse;
import com.klasha.assessment.exception.CustomRuntimeException;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.webjars.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class ApplicationExceptionHandler  extends ResponseEntityExceptionHandler {

//    @ExceptionHandler({NotFoundException.class,ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
//    public ResponseEntity<Object> handleResourceNotFoundException(@NotNull RuntimeException ex) {
//        ErrorResponse error = new ErrorResponse(ex.getMessage(),Arrays.asList(ex.getMessage()));
//        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//    }
    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(@NotNull RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(),Arrays.asList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

//    public ResponseEntity<Object> MethodArgumentTypeMismatchException(@NotNull RuntimeException ex) {
//        ErrorResponse error = new ErrorResponse(ex.getMessage(),Arrays.asList(ex.getMessage()));
//        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//    }
//    @Override
//    @ExceptionHandler( {ConstraintViolationException.class})
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpHeaders headers,
//            HttpStatusCode status,
//            WebRequest request) {
//
//        // Get the validation errors
//        List<String> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(FieldError::getDefaultMessage)
//                .collect(Collectors.toList());
//
//        // Create a custom error response object
//        ErrorResponse errorResponse = new ErrorResponse("Validation Error",errors);
//
//
//        // Return the custom error response with a status code of 400
//        return ResponseEntity.badRequest().body(errorResponse);
//    }
}
