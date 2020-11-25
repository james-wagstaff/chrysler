package com.identifix.contentlabelingservice.listener

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.contentlabelingservice.handler.LabelingMessageHandler
import com.identifix.crawlermqutils.configuration.MessageQueueLibrary
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.crawlermqutils.handler.MessageHandlerStatus
import com.identifix.crawlermqutils.listener.MessageContext
import com.identifix.crawlermqutils.sender.Sender
import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import spock.lang.Specification

class LabelingListenerSpec extends Specification {
    LabelingListener systemUnderTest
    CachingConnectionFactory mockCachingConnectionFactory = Mock(CachingConnectionFactory)
    MessageQueueLibrary mockMessageQueueLibrary = Mock(MessageQueueLibrary)
    MessageHandlerResponse mockMessageHandlerResponse = Mock(MessageHandlerResponse)
    Sender mockErrorSender = Mock(Sender)
    Message mockMessage = Mock(Message)
    Channel mockChannel = Mock(Channel)
    LabelingMessageHandler labelingMessageHandler = Mock(LabelingMessageHandler)
    LabelingServiceConfig labelingServiceConfig = Mock(LabelingServiceConfig)
    String queue = "queueName"
    long tag = 123L
    MessageContext messageContext

    void setup() {
        systemUnderTest = new LabelingListener(mockCachingConnectionFactory, mockMessageQueueLibrary, labelingServiceConfig, labelingMessageHandler, mockErrorSender)
        messageContext = new MessageContext(queue, mockChannel, tag)
        mockMessage.body >> ""
        labelingMessageHandler.handleMessage(_ as Message, _ as MessageContext) >> mockMessageHandlerResponse
    }

    def "When a message is received the handler is called."() {
        when:
        systemUnderTest.receiveGenerate(mockMessage, mockChannel, queue, tag)
        then:
        1 * mockMessageHandlerResponse.status >> MessageHandlerStatus.MESSAGE_PROCESSED
        1 * mockChannel.basicAck(tag, false)
    }
}
