package com.identifix.contentlabelingservice.label

import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import org.springframework.amqp.core.Message
import org.springframework.amqp.support.converter.MessageConversionException
import org.springframework.stereotype.Component

@Component
@SuppressWarnings('ThrowRuntimeException')
class UnknownLabelMaker implements LabelMaker {
    @Override
    MessageHandlerResponse handleMessage(Message message) {
        throw new MessageConversionException('Could not handle message.')
    }
}
