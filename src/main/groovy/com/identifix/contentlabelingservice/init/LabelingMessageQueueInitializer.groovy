package com.identifix.contentlabelingservice.init

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.crawlermqutils.configuration.MessageQueueLibrary
import com.identifix.crawlermqutils.init.AbstractMessageQueueInitializer
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LabelingMessageQueueInitializer extends AbstractMessageQueueInitializer {
    LabelingServiceConfig labelingServiceConfig
    MessageQueueLibrary messageQueueLibrary

    @Autowired
    LabelingMessageQueueInitializer(ConnectionFactory connectionFactory,
                                   MessageQueueLibrary messageQueueLibrary,
                                    LabelingServiceConfig labelingServiceConfig) {
        super(connectionFactory)
        this.messageQueueLibrary = messageQueueLibrary
        this.labelingServiceConfig = labelingServiceConfig
    }

    @Override
    void setUpMessageQueueComponents() {
        setUpExchanges(labelingServiceConfig.generateExchangeName, labelingServiceConfig.completeExchangeName)

        setUpQueues(labelingServiceConfig.generateQueueName)

        bind(labelingServiceConfig.generateQueueName, labelingServiceConfig.generateExchangeName)
    }
}
