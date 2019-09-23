package com.identifix.contentlabelingservice.error

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        List<String> errors = []
        ex.getBindingResult().getFieldErrors().each {errors.add(it.getDefaultMessage())}

        Map<String, Object> body = [:]
        body.put("timestamp", new Date())
        body.put("status", status.value())
        body.put("errors", errors)

        new ResponseEntity<>(body, headers, status)
    }
}
