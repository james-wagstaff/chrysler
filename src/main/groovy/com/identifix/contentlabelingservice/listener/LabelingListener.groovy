package com.identifix.contentlabelingservice.listener

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.contentlabelingservice.handler.LabelingMessageHandler
import com.identifix.crawlermqutils.configuration.MessageQueueLibrary
import com.identifix.crawlermqutils.listener.Listener
import com.identifix.crawlermqutils.listener.MessageContext
import com.identifix.crawlermqutils.sender.Sender
import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
@EnableRabbit
class LabelingListener extends Listener {
    LabelingServiceConfig labelingServiceConfig

    @Autowired
    LabelingListener(CachingConnectionFactory cachingConnectionFactory,
                     MessageQueueLibrary library,
                     LabelingServiceConfig labelingServiceConfig,
                     LabelingMessageHandler messageHandler, Sender sender) {
        super(cachingConnectionFactory, library, messageHandler, sender)
        this.labelingServiceConfig = labelingServiceConfig
    }

    @RabbitListener(queues = '#{labelingServiceConfig.generateQueueName}', returnExceptions = "true")
    void receiveGenerate(Message message,
                     Channel channel,
                     @Header(AmqpHeaders.CONSUMER_QUEUE) String queue,
                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        handleMessage(message, new MessageContext(queue:queue, channel:channel, tag:tag))
    }
}
