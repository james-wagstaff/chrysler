package com.identifix.contentlabelingservice.label.toyota

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
class ToyotaLabelMaker extends AbstractLabelMaker {
    Class messageClass = ToyotaManualMessage
    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        ToyotaManualMessage toyotaManualMessage = message as ToyotaManualMessage
        Manual manual = krakenClient.getManuals(message.year, message.model, null)
                .findAll { it.manualId == toyotaManualMessage.manualId }.last()
        byte[] tocXml = krakenClient.getManualBytes(manual)
        String repairManualCsv = krakenClient.buildToyotaManualLabelingCsv(message.year, message.model, toyotaManualMessage.manualType, tocXml)
        labelCsv(repairManualCsv, 'Toyota', toyotaManualMessage.manualType, "${message.manualId} ${manual.title}")
        SUCCESS
    }

    @Override
    String headerValue(Document document) {
        cleanToc(document.tocpath).split(TOC_SPLIT)[2]
    }

    @Override
    String titleValue(Document document) {
        document.title
    }
}

class ToyotaManualMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/TOYOTA_MANUAL_LABEL/)
    String crawlerTypeKey
    @NotNull
    String manualType
}
