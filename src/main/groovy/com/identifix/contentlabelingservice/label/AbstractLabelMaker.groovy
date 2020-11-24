package com.identifix.contentlabelingservice.label

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.contentlabelingservice.model.Document
import com.identifix.contentlabelingservice.service.CompleteNotifierService
import com.identifix.contentlabelingservice.service.GitService
import com.identifix.contentlabelingservice.service.LabelService
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.crawlermqutils.handler.MessageHandlerStatus
import com.identifix.kraken.client.KrakenClient
import com.identifix.kraken.client.bean.Credential
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Autowired

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

    @Lazy(soft=true)
    KrakenClient krakenClient = {
        Credential credential = new Credential(securityServiceUrl:labelingServiceConfig.securityServiceUri,
                clientId:labelingServiceConfig.clientId,
                clientSecret:labelingServiceConfig.clientSecret)

        new KrakenClient(krakenServiceUrl:labelingServiceConfig.krakenUri, credential:credential)
    } ()

    static final MessageHandlerResponse SUCCESS = new MessageHandlerResponse(MessageHandlerStatus.MESSAGE_PROCESSED)

    MessageHandlerResponse handleMessage(Message message) {
        MessageHandlerResponse messageHandlerResponse = getValidMessageOrNull(message)?.with { LabelContent(it as LabelMakerMessage) }
        if (messageHandlerResponse == SUCCESS) {
            completeNotifierService.notifyComplete(message)
        }
        messageHandlerResponse
    }

    abstract MessageHandlerResponse LabelContent(LabelMakerMessage message) // Contract is that the object's class is messageClass

    private Object getValidMessageOrNull(Message message) {
        messageConverter.convertMessage(message, messageClass)
    }

    @SuppressWarnings('FactoryMethodName')
    protected
    static Document createDocument(String docValues) {
        String[] docValueArray = docValues.split(",")
        if (docValueArray.size() == 7) {
            new Document(docValueArray[0], docValueArray[1], docValueArray[2], docValueArray[3], docValueArray[4] + docValueArray[5], docValueArray[6])
        } else {
            new Document(docValueArray[0], docValueArray[1], docValueArray[2], docValueArray[3], docValueArray[4], docValueArray[5])
        }
    }

    abstract void labelCsv(String Csv, String title)
    abstract Class getMessageClass()
}
