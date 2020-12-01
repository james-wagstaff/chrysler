package com.identifix.contentlabelingservice.label.ford

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.contentlabelingservice.label.MessageConverter
import com.identifix.contentlabelingservice.service.CompleteNotifierService
import com.identifix.contentlabelingservice.service.GitService
import com.identifix.contentlabelingservice.service.LabelService
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.kraken.client.KrakenClient
import org.springframework.amqp.core.Message
import spock.lang.Specification

class PcedLabelMakerSpec extends Specification {
    PcedLabelMaker systemUnderTest
    LabelService mockLabelService = Mock(LabelService)
    GitService mockGitService = Mock(GitService)
    MessageConverter mockMessageConverter = Mock(MessageConverter)
    CompleteNotifierService mockCompleteNotifierService = Mock(CompleteNotifierService)
    LabelingServiceConfig mockLabelingServiceConfig = Mock(LabelingServiceConfig)
    KrakenClient mockKrakenClient = Mock(KrakenClient)
    String labels = 'Label,title,tocpath,category,linkToPage,nuxeoId\n' +
            ',GENERAL INFORMATION; 2013 - 2018 MY RAV4 [12/2012 - ], General > INTRODUCTION > HOW TO USE THIS MANUAL > GENERAL INFORMATION; 2013 - 2018 MY RAV4 [12/2012 - ], Repair Manual, toyota-crawler-bucket/qa/unzipped/TOYOTA/2013/RM/13-18RAV4_RM30B0U_EN_20-08-10_HTML/RM30B0U/xhtml/RM10000000050E6.html, ec7c3da1-7832-311c-8525-0754a38eef0e\n' +
            ',VEHICLE IDENTIFICATION AND SERIAL NUMBERS; 2013 - 2018 MY RAV4 [12/2012 - ], General > INTRODUCTION > IDENTIFICATION INFORMATION > VEHICLE IDENTIFICATION AND SERIAL NUMBERS; 2013 - 2018 MY RAV4 [12/2012 - ], Repair Manual, toyota-crawler-bucket/qa/unzipped/TOYOTA/2013/RM/13-18RAV4_RM30B0U_EN_20-08-10_HTML/RM30B0U/xhtml/RM10000000050TD.html, 53e11ac3-8b5a-35bb-bcd4-fe57b5328189'

    void setup() {
        systemUnderTest = new PcedLabelMaker().with {
            labelingServiceConfig = mockLabelingServiceConfig
            gitService = mockGitService
            labelService = mockLabelService
            messageConverter = mockMessageConverter
            completeNotifierService = mockCompleteNotifierService
            krakenClient = mockKrakenClient
            it
        }
    }

    def 'handleMessage' () {
        FordPcedMessage message = new FordPcedMessage().with {
            type = 'test'
            year = 'test'
            model = 'test'
            it
        }
        when:
            MessageHandlerResponse actual = systemUnderTest.handleMessage(new Message("".bytes, null))
        then:
            1 * mockMessageConverter.convertMessage(_, _) >> message
            1 * mockKrakenClient.buildPcedLabelingCsv(_, _) >> labels
            1 * mockGitService.uploadCsv(_, _, _, _)
            1 * mockLabelService.createLabel(_) >> "test"
            1 * mockLabelService.createLabel(_) >> null
            1 * mockCompleteNotifierService.notifyComplete(_)
            actual == systemUnderTest.SUCCESS
    }
}