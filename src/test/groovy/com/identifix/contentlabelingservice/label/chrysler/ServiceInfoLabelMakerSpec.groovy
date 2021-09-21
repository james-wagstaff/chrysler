package com.identifix.contentlabelingservice.label.chrysler

import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.model.LabelRequest
import com.identifix.contentlabelingservice.service.GitService
import com.identifix.contentlabelingservice.service.LabelService
import com.identifix.contentlabelingservice.utils.StringUtils
import com.identifix.contentlabelingservice.utils.TocConverter
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import com.identifix.kraken.client.KrakenClient
import com.identifix.kraken.client.bean.Manual
import com.identifix.kraken.client.bean.Vehicle
import org.json.JSONArray
import spock.lang.Specification

class ServiceInfoLabelMakerSpec extends Specification {

    ServiceInfoLabelMaker systemUnderTest = new ServiceInfoLabelMaker()
    KrakenClient mockKrakenClient = Mock(KrakenClient)
    LabelService mockLabelService = Mock(LabelService)
    GitService mockGitService = Mock(GitService)

    void setup() {
        systemUnderTest.krakenClient = mockKrakenClient
        systemUnderTest.labelService = mockLabelService
        systemUnderTest.gitService = mockGitService
    }

    def "test label Content"() {
        given:
            LabelMakerMessage message = new ChryslerServiceInfoMessage(crawlerTypeKey:"CHRYSLER_SERVICE_LABEL", manualType:"Service", year:"2018", model:"Jeep Renegade", manualId:"549859cd-7c58-3270-a0e0-56806b022b69")
            String label = "label1,title1,to> cpa >th1,category1,linkToPage1,nuxeoId1\n\rlabel2,title2,to > cpa> th2,category2,linkToPage2,nuxeoId2"
            Vehicle vehicle= new Vehicle("2017", "Chrysler", "Chrysler Pacifica", "FCA", "3.0" )
            Manual manual = new Manual("24545", "Service", "Chrysler", "516451499161", "Chrysler Pacifica", "", "content" as byte[], vehicle)
        when:
            MessageHandlerResponse result = systemUnderTest.labelContent(message)
        then:
            1 * mockKrakenClient.getManualBytes(_ as Manual) >> "content1".bytes
            1 * mockKrakenClient.getManualById('549859cd-7c58-3270-a0e0-56806b022b69') >> manual
            1 * mockKrakenClient.buildChryslerLabelingCsv('2018', 'Jeep Renegade', [91, 93], 'Service') >> label
            1 * mockLabelService.createLabel(_ as LabelRequest) >> "label"
            1 * mockGitService.uploadCsv(_, _, _, _)
            0 * _
        and:
            result
    }

    def "test remove Duplicate Words"() {
        given:
            String input = "Jeep Jeep Renegade"
        when:
            String  result = StringUtils.removeDuplicateWords(input)
        then:
            0 * _
        and:
            result == "Jeep Renegade"
    }

    def "test to JSONArray Byte byte[] content"() {
        given:
        byte[] content = new String(
                "<item catnbr='00' id='1' title='00 - Vehicle Data '> \n" +
                "    <item catnbr='0001' id='2' title='Vehicle Information '> \n" +
                "        <item compid='00013' id='3' pageCode='c82081b4-a8f0-4e76-b6a8-ca4bd89c32bf' title='International Vehicle Control and Display Symbols Description'> </item> \n" +
                "        <item compid='00025' id='4' pageCode='251126b0-fdcd-4aa6-bf64-3feda024faaa' title='LABEL, Vehicle Emission Certification Information (VECI) Description'> </item> \n" +
                "        <item compid='00027' id='5' pageCode='3cfc4dbc-58f2-484e-afbb-b1a6c1e2de85' title='Vehicle Certification Label Description'> </item> \n" +
                "    </item>\n" +
                "</item>").bytes
        when:
            byte[] result = TocConverter.tocXmlToJson(content)
        then:
            0 * _
        and:
            new JSONArray(new String(result)).length() == 3
    }
}
