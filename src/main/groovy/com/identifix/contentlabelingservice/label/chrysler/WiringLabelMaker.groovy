package com.identifix.contentlabelingservice.label.chrysler

import static com.identifix.contentlabelingservice.utils.TocConverter.tocXmlToJson

import com.identifix.contentlabelingservice.utils.CommonConstants
import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.AbstractLabelMakerMessage
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.Document
import com.identifix.crawlermqutils.handler.MessageHandlerResponse

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

class WiringLabelMaker extends AbstractLabelMaker {

    Class messageClass = ChryslerWiringMessage

    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        byte[] content = krakenClient.getChryslerWiringToc(message.manualId)
        String csvFile = krakenClient.buildChryslerWiringLabelingCsv(message.year, message.model, tocXmlToJson(content))
        labelCsv(csvFile, CommonConstants.PUBLISHER_CHRYSLER, CommonConstants.MANUAL_TYPE_WIRING, message.manualId, message.manualId)

        SUCCESS
    }

    @Override
    Class getMessageClass() {
        return messageClass
    }

    @Override
    String titleValue(Document document) {
        return document.title
    }

    @Override
    String headerValue(Document document) {
        cleanToc(document.tocpath)
    }
}

class ChryslerWiringMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/CHRYSLER_WIRING_LABEL/)
    String crawlerTypeKey
    @NotNull
    String manualType
}
