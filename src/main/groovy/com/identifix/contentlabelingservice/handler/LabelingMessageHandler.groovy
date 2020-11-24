package com.identifix.contentlabelingservice.handler

import com.identifix.contentlabelingservice.label.LabelMaker
import com.identifix.contentlabelingservice.label.UnknownLabelMaker
import com.identifix.crawlermqutils.handler.MessageHandler
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.crawlermqutils.listener.MessageContext
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LabelingMessageHandler implements MessageHandler {

    @Autowired
    List<LabelMaker> allContentLabelMakers

    @Lazy
    List<LabelMaker> sortedContentLabelMakers = {
        allContentLabelMakers.split { it.class != UnknownLabelMaker }.flatten() as List<LabelMaker>
    } ()

    @Override
    MessageHandlerResponse handleMessage(Message message, MessageContext messageContext) {
        sortedContentLabelMakers.findResult { it.handleMessage(message) }
    }
}
