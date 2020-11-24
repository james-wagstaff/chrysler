package com.identifix.contentlabelingservice.init

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.crawlermqutils.configuration.CrawlerException
import com.identifix.crawlermqutils.configuration.MessageQueueLibrary
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import org.springframework.amqp.rabbit.connection.Connection
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import spock.lang.Specification

class LabelingMessageQueueInitializerSpec extends Specification {
    LabelingMessageQueueInitializer systemUnderTest
    ConnectionFactory mockConnectionFactory = Mock(ConnectionFactory)
    Connection mockConnection = Mock(Connection)
    MessageQueueLibrary mockMessageQueueLibrary = Mock(MessageQueueLibrary)
    LabelingServiceConfig labelingServiceConfig = Mock(LabelingServiceConfig)
    Channel mockChannel = Mock(Channel)
    AMQP.Queue.DeclareOk mockDeclareOk = Mock(AMQP.Queue.DeclareOk)
    String testExchangeName = "test-exchange"
    String testCExchangeName = "testC-exchange"
    String testQueueName = "test-queue"

    def setup() {
        systemUnderTest = new LabelingMessageQueueInitializer(mockConnectionFactory, mockMessageQueueLibrary, labelingServiceConfig)
        mockConnectionFactory.createConnection(*_) >> mockConnection
        mockConnection.createChannel(*_) >> mockChannel
        mockChannel.queueDeclare(*_) >> mockDeclareOk
        labelingServiceConfig.generateQueueName >> testQueueName
        labelingServiceConfig.generateExchangeName >> testExchangeName
        labelingServiceConfig.completeExchangeName >> testCExchangeName
    }

    def 'it runs init' () {
        when: 'initialize environment'
        systemUnderTest.initializeMQEnvironment()
        then: 'it runs'
        2 * mockDeclareOk.queue
        4 * mockConnection.close()
        4 * mockChannel.close()
        1 * mockChannel.exchangeDeclare(testExchangeName, 'fanout', true, false, false, [:])
        1 * mockChannel.queueBind(testQueueName, testExchangeName, '', [:])
    }

    def 'can add exchanges' () {
        expect: 'empty to start with'
        systemUnderTest.exchanges.size() == 0
        when: 'add exchange'
        systemUnderTest.setUpExchanges(testExchangeName)
        then: 'exchange is listed'
        systemUnderTest.exchanges.size() == 1
        systemUnderTest.exchanges.containsKey(testExchangeName)
    }

    def 'can add queues' () {
        expect: 'empty to start with'
        systemUnderTest.queues.size() == 0
        when: 'add exchange'
        systemUnderTest.setUpQueues(testQueueName)
        then: 'exchange is listed'
        systemUnderTest.queues.size() == 1
        systemUnderTest.queues.containsKey(testQueueName)
    }

    def 'can bind queues to exchanges'() {
        when: 'set up'
        systemUnderTest.setUpExchanges(testExchangeName)
        systemUnderTest.setUpQueues(testQueueName)
        then: 'bind runs'
        systemUnderTest.bind(testQueueName, testExchangeName) == null
        systemUnderTest.bind(testQueueName, testExchangeName) == null
    }

    def 'fails if queue is not setup'() {
        when: 'set up'
        systemUnderTest.setUpExchanges(testExchangeName)
        systemUnderTest.bind(testQueueName, testExchangeName)
        then: 'boom'
        thrown(CrawlerException)
    }

    def 'fails if exchange is not setup'() {
        when: 'set up'
        systemUnderTest.setUpQueues(testQueueName)
        systemUnderTest.bind(testQueueName, testExchangeName)
        then: 'boom'
        thrown(CrawlerException)
    }
}

