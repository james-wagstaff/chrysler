package com.identifix.contentlabelingservice.label.chrysler

import static com.identifix.contentlabelingservice.utils.TocConverter.tocXmlToJson

import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.AbstractLabelMakerMessage
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.Document
import com.identifix.contentlabelingservice.utils.CommonConstants
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
@Slf4j
class ServiceBulletinLabelMaker extends AbstractLabelMaker {

    Class messageClass = ChryslerServiceBulletinMessage

    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        byte[] content = krakenClient.getServiceBulletinVehicleToc(message.manualId)
        byte[] jsonBytes = tocXmlToJson(content)
            String csvFile = krakenClient.buildChryslerServiceBulletinLabelingCsv(message.year, message.model, jsonBytes)
            labelCsv(csvFile, CommonConstants.PUBLISHER_CHRYSLER, CommonConstants.MANUAL_TYPE_BULLETIN, message.manualId, message.manualId)
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

class ChryslerServiceBulletinMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/CHRYSLER_SERVICE_BULLETIN_LABEL/)
    String crawlerTypeKey
    @NotNull
    String manualType
}

