package com.sunil.myretail.exception;

import com.sunil.myretail.price.exception.GetPriceException;
import com.sunil.myretail.price.exception.PriceCreateException;
import com.sunil.myretail.price.exception.PriceUpdateException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationClientErrorException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationProductNotFoundException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class MyRetailGlobalExceptionHandler {


    @ExceptionHandler(value
            = {RedSkyIntegrationProductNotFoundException.class})
    protected ResponseEntity<MyRetailApiError> handleProductNotFoundException(
            RuntimeException ex, HttpServletRequest request) {

        log.error("api_event=controller_error http_method={} api_path={} http_status={}",
                request.getMethod(),
                request.getServletPath(),
                HttpStatus.NOT_FOUND
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(buildMyRetailApiError(ex, HttpStatus.NOT_FOUND),
                headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value
            = {RedSkyIntegrationClientErrorException.class, ValidationException.class})
    protected ResponseEntity<Object> handleBadProductException(
            RuntimeException ex, HttpServletRequest request) {

        log.error("api_event=controller_error http_method={} api_path={} http_status={}",
                request.getMethod(),
                request.getServletPath(),
                HttpStatus.BAD_REQUEST
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(buildMyRetailApiError(ex, HttpStatus.BAD_REQUEST),
                headers, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value
            = {RedSkyIntegrationServerErrorException.class,
            PriceCreateException.class,
            PriceUpdateException.class,
            GetPriceException.class})
    protected ResponseEntity<MyRetailApiError> handleAppIntegrationException(
            RuntimeException ex, HttpServletRequest request) {

        logInternalServerError(log,
                "api_event=controller_error http_method={} api_path={} http_status={} AppIntegrationException",
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(buildMyRetailApiError(ex, HttpStatus.INTERNAL_SERVER_ERROR),
                headers, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<MyRetailApiError> defaultErrorHandler(Exception exception, HttpServletRequest request) {
        RuntimeException unHandledException = new RuntimeException("Something went wrong...");

        logInternalServerError(log,
                "api_event=controller_error http_method={} api_path={} http_status={} defaultErrorHandler",
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(buildMyRetailApiError(unHandledException, HttpStatus.INTERNAL_SERVER_ERROR),
                headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logInternalServerError(Logger log, String messageFormat, HttpServletRequest request,
                                        HttpStatus status, Exception exp) {
        log.error(messageFormat,
                request.getMethod(),
                request.getServletPath(),
                status,
                exp
        );
    }

    private MyRetailApiError buildMyRetailApiError(RuntimeException ex, HttpStatus httpStatus) {
        MyRetailApiError retailApiError = new MyRetailApiError();
        retailApiError.setError(ex.getMessage());
        retailApiError.setStatus(httpStatus.value());
        return retailApiError;
    }


}

