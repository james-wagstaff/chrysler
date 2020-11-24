package com.identifix.contentlabelingservice.label.ford

import com.identifix.contentlabelingservice.label.AbstractLabelMaker
import com.identifix.contentlabelingservice.label.AbstractLabelMakerMessage
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.Document
import com.identifix.contentlabelingservice.model.LabelRequest
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
    MessageHandlerResponse LabelContent(LabelMakerMessage message) {
        String csv = krakenClient.buildPcedLabelingCsv(message.year, (message as FordPcedMessage).type)
        labelCsv(csv, "${message.year}-${(message as FordPcedMessage).type}")
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
            request.publisher = "Ford"
            request.manualType = "PCED" //document.category
            request.title = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[3]
            request.header = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[2]
            request.refresh = refresh

            if (refresh) {
                refresh = false
            }

            String label  = labelService.createLabel(request)
            document.label = label ? label : "Not Found"

            if (label) {
                totalLabelsFound++
            }

            labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
        }

        log.info("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")

        gitService.uploadCsv(labelSpreadsheet as String, "Ford","Pced", "${title} PCED Manual")
    }
}

class FordPcedMessage extends AbstractLabelMakerMessage {
    @NotNull
    @Pattern(regexp=/FORD_PCED_LABEL/)
    String crawlerTypeKey
    @NotNull
    String type
}
