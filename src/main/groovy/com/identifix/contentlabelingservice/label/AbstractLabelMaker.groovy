package com.identifix.contentlabelingservice.label

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.contentlabelingservice.model.Document
import com.identifix.contentlabelingservice.model.LabelRequest
import com.identifix.contentlabelingservice.service.CompleteNotifierService
import com.identifix.contentlabelingservice.service.GitService
import com.identifix.contentlabelingservice.service.LabelService
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.crawlermqutils.handler.MessageHandlerStatus
import com.identifix.kraken.client.KrakenClient
import com.identifix.kraken.client.bean.Credential
import groovy.util.logging.Slf4j
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
@SuppressWarnings(['DuplicateNumberLiteral'])
abstract class AbstractLabelMaker implements LabelMaker {
    @Autowired
    LabelService labelService

    @Autowired
    GitService gitService

    @Autowired
    MessageConverter messageConverter

    @Autowired
    CompleteNotifierService completeNotifierService

    @Autowired
    LabelingServiceConfig labelingServiceConfig

    static final String TOC_SPLIT = '\\s*>\\s*'

    @Lazy(soft=true)
    KrakenClient krakenClient = {
        Credential credential = new Credential(securityServiceUrl:labelingServiceConfig.securityServiceUri,
                clientId:labelingServiceConfig.clientId,
                clientSecret:labelingServiceConfig.clientSecret)

        new KrakenClient(krakenServiceUrl:labelingServiceConfig.krakenUri, credential:credential)
    } ()

    static final MessageHandlerResponse SUCCESS = new MessageHandlerResponse(MessageHandlerStatus.MESSAGE_PROCESSED)

    MessageHandlerResponse handleMessage(Message message) {
        MessageHandlerResponse messageHandlerResponse = getValidMessageOrNull(message)?.with { labelContent(it as LabelMakerMessage) }
        if (messageHandlerResponse == SUCCESS) {
            completeNotifierService.notifyComplete(message)
        }
        messageHandlerResponse
    }

    abstract MessageHandlerResponse labelContent(LabelMakerMessage message) // Contract is that the object's class is messageClass

    private Object getValidMessageOrNull(Message message) {
        messageConverter.convertMessage(message, messageClass)
    }

    @SuppressWarnings(['FactoryMethodName'])
    protected
    static Document createDocument(String docValues) {
        String[] docValueArray = docValues.split(",")
        if (docValueArray.size() == 7) {
            new Document(docValueArray[0], docValueArray[1], docValueArray[2], docValueArray[3], docValueArray[4] + docValueArray[5], docValueArray[6])
        } else {
            new Document(docValueArray[0], docValueArray[1], docValueArray[2], docValueArray[3], docValueArray[4], docValueArray[5])
        }
    }

    void labelCsv(String csv, String publisher, String manualType, String manualId, String title) {
        int totalDocs = 0
        int totalLabelsFound = 0
        boolean refresh = true
        StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
        csv.split("\\r?\\n").drop(1).each {
            totalDocs++
            Document document = createDocument(it)
            LabelRequest request = new LabelRequest()
            request.publisher = publisher
            request.manualType = manualType //document.category
            request.title = titleValue(document)
            request.header = headerValue(document)
            request.refresh = refresh

            if (refresh) {
                refresh = false
            }

            String label  = labelService.createLabel(request)
            document.label = label ?: "Not Found"

            if (label) {
                totalLabelsFound++
            }

            labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
        }

        log.info("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")

        gitService.uploadCsv(labelSpreadsheet as String, publisher, manualType, "${manualId} ${title}")
    }

    String cleanToc(String toc) {
        toc.replaceAll("\\[", "").replaceAll("]", "")
    }

    abstract Class getMessageClass()
    abstract String titleValue(Document document)
    abstract String headerValue(Document document)
}
