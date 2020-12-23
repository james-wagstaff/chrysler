package com.identifix.contentlabelingservice.label.ford

import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.AbstractLabelMakerMessage
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.Document
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
@Slf4j
class PcedLabelMaker extends AbstractLabelMaker {
    Class messageClass = FordPcedMessage

    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        String csv = krakenClient.buildPcedLabelingCsv(message.year, (message as FordPcedMessage).type)
        labelCsv(csv, 'Ford', 'PCED', message.manualId, "${message.year}-${(message as FordPcedMessage).type} PCED Manual")
        SUCCESS
    }

    @Override
    String titleValue(Document document) {
        cleanToc(document.tocpath).split(TOC_SPLIT)[3]
    }

    @Override
    String headerValue(Document document) {
        cleanToc(document.tocpath).split(TOC_SPLIT)[2]
    }
}

class FordPcedMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/FORD_PCED_LABEL/)
    String crawlerTypeKey
    @NotNull
    String type
}
