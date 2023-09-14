package com.klasha.assessment.exception;

public class CityPopulationException extends RuntimeException {

    public CityPopulationException(String message){
        super(message);
    }

    public CityPopulationException(String message, Throwable cause){
        super(message, cause);
    }
    
}