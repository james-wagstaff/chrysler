package com.identifix.contentlabelingservice.label.ford

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
class WiringLabelMaker extends AbstractLabelMaker {
    Class messageClass = FordWiringMessage

    @Override
    MessageHandlerResponse LabelContent(LabelMakerMessage message) {
        Manual manual = krakenClient.getManuals(message.year, message.model, null).findAll { it.publisherManualCategory.toLowerCase() == "wiring" } [0]
        byte[] tocJson = krakenClient.getManualBytes(manual)
        String wiringCsv = krakenClient.buildWiringLabelingCsv(message.year, message.model, tocJson)
        labelCsv(wiringCsv, manual.title)
        SUCCESS
    }

    @Override
    void labelCsv(String csv, String title) {
        int totalDocs = 0
        int totalLabelsFound = 0
        boolean refresh = true

        StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
        csv.split("\\r?\\n").drop(1).each {
            totalDocs++
            Document document = createDocument(it)
            LabelRequest request = new LabelRequest()
            request.publisher = "Ford"
            request.manualType = "Wiring" //document.category
            request.title = document.title
            request.refresh = refresh

            if (refresh) {
                refresh = false
            }
            request.header = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[1]
            String label  = labelService.createLabel(request)
            document.label = label ? label : "Not Found"

            if (label) {
                totalLabelsFound++
            }

            labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
        }

        log.info("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")

        gitService.uploadCsv(labelSpreadsheet as String, "Ford","Wiring", title)
    }
}

class FordWiringMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/FORD_WIRING_LABEL/)
    String crawlerTypeKey
}
