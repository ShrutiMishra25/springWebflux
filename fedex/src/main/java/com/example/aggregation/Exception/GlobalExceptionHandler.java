package com.example.aggregation.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ControllerAdvice
    public class AggregationExceptionHandler {

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
        @ResponseBody
        public ErrorInfo handleMethodNotSupported(WebRequest request) {
            return new ErrorInfo(request.getContextPath(), "HTTP request method not supported for this operation.");
        }

        @ExceptionHandler(IOException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ResponseBody
        public ErrorInfo handleIOException(WebRequest request, Exception ex) {
            return new ErrorInfo(request.getContextPath(), "IO Error: " + ex.getMessage());
        }

        @ExceptionHandler(ServiceException.class)
        @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
        @ResponseBody
        public ErrorInfo handleServiceException(WebRequest request, Exception ex) {
            return new ErrorInfo(request.getContextPath(), "Service Exception: " + ex.getMessage());
        }

        @ExceptionHandler(RuntimeException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ResponseBody
        public ErrorInfo handleRuntimeException(WebRequest request, Exception ex) {
            return new ErrorInfo(request.getContextPath(), "Runtime Exception: " + ex.getMessage());
        }

    }
}
