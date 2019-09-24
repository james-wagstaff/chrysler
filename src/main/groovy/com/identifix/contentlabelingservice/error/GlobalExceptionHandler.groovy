package com.identifix.contentlabelingservice.error

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        List<String> errors = []
        ex.bindingResult.fieldErrors.each { errors.add(it.defaultMessage) }
        new ResponseEntity<>(error(status.value(), errors), headers, status)
    }

    @ExceptionHandler(BitBucketNetworkException)
    ResponseEntity bitBucketNetworkException(final BitBucketNetworkException e) {
        List<String> errors = [e.message]
        new ResponseEntity(error(HttpStatus.SERVICE_UNAVAILABLE.value(), errors), HttpStatus.SERVICE_UNAVAILABLE)
    }

    private static Map<String, Object> error(int status, List<String> errors) {
        Map<String, Object> body = [:]
        body.put("timestamp", new Date())
        body.put("status", status)
        body.put("errors", errors)
        body
    }
}
