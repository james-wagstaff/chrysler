package com.identifix.contentlabelingservice.label

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.validation.Validation
import javax.validation.Validator

@Component
class MessageConverter {

    @Autowired
    ObjectMapper objectMapper

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator()

    @SuppressWarnings('ParameterName')
    <T> T convertMessage(Message message, Class T) {
        try {
            T convertedMessage = objectMapper.readValue(message.body, T) as T
            validator.validate(convertedMessage) ? null : convertedMessage // validate() hands back a list of errors
        } catch (ignored) {
            null
        }
    }
}
