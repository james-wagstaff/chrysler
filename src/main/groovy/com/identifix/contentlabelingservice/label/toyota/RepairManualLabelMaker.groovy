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
class RepairManualLabelMaker extends AbstractLabelMaker {
    Class messageClass = ToyotaRepairManualMessage
    @Override
    MessageHandlerResponse labelContent(LabelMakerMessage message) {
        Manual manual = krakenClient.getManuals(message.year, message.model, null)
                .findAll { it.publisherManualCategory.toLowerCase() == "repair manual" && (!(message as ToyotaRepairManualMessage).manualId || it.manualId == (message as ToyotaRepairManualMessage).manualId) }.last()
        byte[] tocXml = krakenClient.getManualBytes(manual)
        String repairManualCsv = krakenClient.buildRepairManualLabelingCsv(message.year, message.model, tocXml)
        labelCsv(repairManualCsv, 'Toyota', 'Repair Manual', manual.title)
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

class ToyotaRepairManualMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/TOYOTA_REPAIR_MANUAL_LABEL/)
    String crawlerTypeKey
    String manualId
}
