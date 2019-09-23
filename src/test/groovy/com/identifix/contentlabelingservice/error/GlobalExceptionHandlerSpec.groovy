package com.identifix.contentlabelingservice.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import spock.lang.Specification

class GlobalExceptionHandlerSpec extends Specification {
    def "ArgumentNotValidException is thrown" () {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler()
        BindingResult bindingResult = Mock()
        FieldError fieldError = new FieldError("test", "test", "test")
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult)
        HttpStatus status = HttpStatus.BAD_REQUEST
        when:
            ResponseEntity responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(ex, null, status, null)
        then:
            1 * bindingResult.fieldErrors >> [fieldError]
            responseEntity.statusCode == status
            ((responseEntity.body as LinkedHashMap).get("errors") as ArrayList<String>).get(0).equalsIgnoreCase("test")
    }
}
