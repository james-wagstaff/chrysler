package com.identifix.contentlabelingservice.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.identifix.contentlabelingservice.label.LabelMaker
import com.identifix.contentlabelingservice.label.chrysler.ServiceBulletinLabelMaker
import com.identifix.crawlermqutils.handler.MessageHandlerResponse
import spock.lang.Specification

class ServiceBulletinControllerSpec extends Specification {

    String supportServiceReponse = "[{\"year\":\"2020\",\"model\":\" RAM 1500 Pickup\",\"make\":\"Chrysler\",\"tocName\":\"2020DSCHRYSLERRAM1500PICKUP\",\"platform\":\"DS\"},{\"year\":\"2020\",\"model\":\" Jeep Compass\",\"make\":\"Chrysler\",\"tocName\":\"2020MVCHRYSLERJEEPCOMPASS\",\"platform\":\"MV\"},{\"year\":\"2020\",\"model\":\" Jeep Wrangler\",\"make\":\"Chrysler\",\"tocName\":\"2020JLCHRYSLERJEEPWRANGLER\",\"platform\":\"JL\"},{\"year\":\"2021\",\"model\":\" RAM 1500 Pickup\",\"make\":\"Chrysler\",\"tocName\":\"2021DSCHRYSLERRAM1500PICKUP\",\"platform\":\"DS\"},{\"year\":\"2021\",\"model\":\" Jeep Wrangler\",\"make\":\"Chrysler\",\"tocName\":\"2021JLCHRYSLERJEEPWRANGLER\",\"platform\":\"JL\"},{\"year\":\"2021\",\"model\":\" Jeep Gladiator\",\"make\":\"Chrysler\",\"tocName\":\"2021JTCHRYSLERJEEPGLADIATOR\",\"platform\":\"JT\"},{\"year\":\"2020\",\"model\":\" Jeep Wrangler\",\"make\":\"Chrysler\",\"tocName\":\"2020JLCHRYSLERJEEPWRANGLER\",\"platform\":\"JL\"}]"
    ServiceBulletinController systemUnderTest = Spy()
    LabelMaker mockLabelMaker = Mock(ServiceBulletinLabelMaker)

    def setup() {
        systemUnderTest.mapper = new ObjectMapper()
        systemUnderTest.labelMaker = mockLabelMaker
    }

    def "parseVehicleList returns a parsed list of VehicleDto's"() {
        given:
            List<VehicleDto> result = systemUnderTest.parseVehicleList(supportServiceReponse)
        expect:
            result
            result.size() > 0
    }

    def "ProcessVehicles correctly separates passed and failing result cases"() {
        setup:
            List<VehicleDto> vehicles = systemUnderTest.parseVehicleList(supportServiceReponse)
            MessageHandlerResponse response = new MessageHandlerResponse()
        when:
            Map<String, List<String>> result = systemUnderTest.processVehicles(vehicles)
        then:
            7 * systemUnderTest.labelMaker.labelContent(_) >>> [response, response, response, response] >> { throw new Exception() }
        expect:
            result
            result['processedTocs'].size() == 4
            result['failedTocs'].size() == 3
    }
}
