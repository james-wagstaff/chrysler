package com.identifix.contentlabelingservice.label

import com.fasterxml.jackson.databind.ObjectMapper
import com.identifix.contentlabelingservice.label.toyota.ToyotaRepairManualMessage
import org.springframework.amqp.core.Message
import spock.lang.Specification

class MessageConverterSpec extends Specification {
    MessageConverter systemUnderTest = new MessageConverter().with {
        objectMapper = new ObjectMapper()
        it
    }

    Message messageSuccess
    Message messageFailed

    void setup() {
        messageSuccess = new Message('{"crawlerTypeKey":"TOYOTA_REPAIR_MANUAL_LABEL","year":"2018","model":"RAV4"}'.bytes, null)
        messageFailed = new Message(''.bytes, null)
    }

    def 'convert message success'() {
        when:
            ToyotaRepairManualMessage message = systemUnderTest.convertMessage(messageSuccess, ToyotaRepairManualMessage)
        then:
            message.crawlerTypeKey == 'TOYOTA_REPAIR_MANUAL_LABEL'
            message.year == '2018'
            message.model == 'RAV4'
    }

    def 'convert message fail'() {
        when:
            ToyotaRepairManualMessage message = systemUnderTest.convertMessage(messageFailed, ToyotaRepairManualMessage)
        then:
            !message
    }
}
