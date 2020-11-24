package com.identifix.contentlabelingservice.label.toyota

import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.AbstractLabelMakerMessage
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.Document
import com.identifix.contentlabelingservice.model.LabelRequest
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
    MessageHandlerResponse LabelContent(LabelMakerMessage message) {
        Manual manual = krakenClient.getManuals(message.year, message.model, null)
                .findAll { it.publisherManualCategory.toLowerCase() == "repair manual" && (!(message as ToyotaRepairManualMessage).manualId || it.manualId == (message as ToyotaRepairManualMessage).manualId)}.last()
        byte[] tocXml = krakenClient.getManualBytes(manual)
        String repairManualCsv = krakenClient.buildRepairManualLabelingCsv(message.year, message.model, tocXml)
        labelCsv(repairManualCsv, manual.title)
        SUCCESS
    }

    @Override
    void labelCsv(String Csv, String title) {
        int totalDocs = 0
        int totalLabelsFound = 0
        boolean refresh = true
        StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
        Csv.split("\\r?\\n").drop(1).each {
            totalDocs++
            Document document = createDocument(it)
            LabelRequest request = new LabelRequest()
            request.publisher = "Toyota"
            request.manualType = "Repair Manual"
            request.title = document.title
            request.refresh = refresh

            if (refresh) {
                refresh = false
            }

            try {
                request.header = document.tocpath.split(" > ")[2]
            } catch(Exception e) {
                log.error(e.message)
            }
            String label  = labelService.createLabel(request)
            document.label = label ? label : "Not Found"

            if (label) {
                totalLabelsFound++
            }

            labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
        }

         log.info("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")

        gitService.uploadCsv(labelSpreadsheet as String, "Toyota","Repair Manual", title)
    }
}

class ToyotaRepairManualMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/TOYOTA_REPAIR_MANUAL_LABEL/)
    String crawlerTypeKey
    String manualId
}
