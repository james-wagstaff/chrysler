package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.crawlermqutils.sender.Sender
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CompleteNotifierService {
    @Autowired
    Sender sender

    @Autowired
    LabelingServiceConfig labelingServiceConfig

    void notifyComplete(Message message) {
        sender.sendMessage(labelingServiceConfig.completeExchangeName, new String(message.body))
    }
}
