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
class WorkshopLabelMaker extends AbstractLabelMaker {
    Class messageClass = FordWorkshopMessage

    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        String csv = krakenClient.buildWorkshopLabelingCsv(message.year, message.model)
        labelCsv(csv, "Ford", "Workshop", message.manualId, "${message.year}-${message.model} Workshop Manual")
        SUCCESS
    }

    @Override
    String titleValue(Document document) {
        cleanToc(document.tocpath).split(TOC_SPLIT).last()

    }

    @Override
    String headerValue(Document document) {
        cleanToc(document.tocpath).split(TOC_SPLIT)[4]
    }
}

class FordWorkshopMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/FORD_WORKSHOP_LABEL/)
    String crawlerTypeKey
}
