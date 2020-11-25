package com.identifix.contentlabelingservice.label

import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import org.springframework.amqp.core.Message

interface LabelMaker {
    MessageHandlerResponse handleMessage(Message message)
}
