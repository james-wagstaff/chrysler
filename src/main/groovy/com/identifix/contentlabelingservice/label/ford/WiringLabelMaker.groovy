package com.identifix.contentlabelingservice.label.ford

import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.AbstractLabelMakerMessage
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.Document
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.kraken.client.bean.Manual
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
@Slf4j
class WiringLabelMaker extends AbstractLabelMaker {
    Class messageClass = FordWiringMessage

    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        Manual manual = krakenClient.getManuals(message.year, message.model, null).findAll { it.manualId == message.manualId } [0]
        byte[] tocJson = krakenClient.getManualBytes(manual)
        String wiringCsv = krakenClient.buildWiringLabelingCsv(message.year, message.model, tocJson)
        labelCsv(wiringCsv, 'Ford', 'Wiring', message.manualId, "${message.manualId} ${manual.title}")
        SUCCESS
    }

    @Override
    String titleValue(Document document) {
        document.title
    }

    @Override
    String headerValue(Document document) {
        cleanToc(document.tocpath).split(TOC_SPLIT)[1]
    }
}

class FordWiringMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/FORD_WIRING_LABEL/)
    String crawlerTypeKey
}
