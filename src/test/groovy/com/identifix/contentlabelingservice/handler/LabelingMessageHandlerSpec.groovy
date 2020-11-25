package com.identifix.contentlabelingservice.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.LabelMaker
import com.identifix.contentlabelingservice.label.UnknownLabelMaker
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.crawlermqutils.listener.MessageContext
import org.springframework.amqp.core.Message
import spock.lang.Specification

class LabelingMessageHandlerSpec extends Specification {

    LabelingMessageHandler systemUnderTest

    LabelMaker[] labelMakers
    MessageHandlerResponse mockMessageHandlerResponse = Mock()
    MessageContext mockMessageContext = Mock()
    Message mockMessage = Mock()

    ObjectMapper objectMapper = new ObjectMapper()

    static final Map TEST_MAP = [key:'value']

    void setup() {
        labelMakers = [new UnknownLabelMaker(), (1..5).collect { Mock(AbstractLabelMaker) } ].flatten() as LabelMaker[]
        systemUnderTest = new LabelingMessageHandler(allContentLabelMakers:labelMakers)
    }

    def "should try to use each content labelMaker until one responds"() {
        given: "A message with a map payload"
        byte[] messageBytes = objectMapper.writeValueAsString(TEST_MAP).bytes
        mockMessage.body >> messageBytes
        when: "The message is handled"
        MessageHandlerResponse actualResponse = systemUnderTest.handleMessage(mockMessage, mockMessageContext)
        then: "The response from the first content publisher that could handle the message is returned"
        actualResponse == mockMessageHandlerResponse
        and: "Each content publisher was tried in turn, only until one returned a response"
        1 * labelMakers[1].handleMessage(mockMessage)
        1 * labelMakers[2].handleMessage(mockMessage)
        1 * labelMakers[3].handleMessage(mockMessage) >> mockMessageHandlerResponse
        0 * labelMakers[3].handleMessage(_)
        0 * labelMakers[4].handleMessage(_)
        0 * labelMakers[5].handleMessage(_)
    }

    def "should return failure message if no content label responds"() {
        given: "A message with a map payload"
        byte[] messageBytes = objectMapper.writeValueAsString(TEST_MAP).bytes
        mockMessage.body >> messageBytes
        when: "The message is handled"
        systemUnderTest.handleMessage(mockMessage, mockMessageContext)
        then: "A failure response containing the offending message is returned"
        thrown(RuntimeException)
    }
}
