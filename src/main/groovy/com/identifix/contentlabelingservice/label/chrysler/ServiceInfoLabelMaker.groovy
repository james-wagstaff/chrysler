package com.identifix.contentlabelingservice.label.chrysler

import static com.identifix.contentlabelingservice.utils.TocConverter.tocXmlToJson
import static com.identifix.contentlabelingservice.utils.StringUtils.removeDuplicateWords

import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.AbstractLabelMakerMessage
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.Document
import com.identifix.contentlabelingservice.utils.CommonConstants
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.kraken.client.bean.Manual
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
@Slf4j
class ServiceInfoLabelMaker extends AbstractLabelMaker {

    Class messageClass = ChryslerServiceInfoMessage

    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        Manual manual = krakenClient.getManualById(message.manualId)
        byte[] content = krakenClient.getManualBytes(manual)
        String csvFile = krakenClient.buildChryslerLabelingCsv(message.year, message.model, tocXmlToJson(content), CommonConstants.MANUAL_TYPE_SERVICE)
        labelCsv(csvFile, CommonConstants.PUBLISHER_CHRYSLER, CommonConstants.MANUAL_TYPE_SERVICE, message.manualId, removeDuplicateWords(manual.title))
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

class ChryslerServiceInfoMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/CHRYSLER_SERVICE_LABEL/)
    String crawlerTypeKey
    @NotNull
    String manualType
}