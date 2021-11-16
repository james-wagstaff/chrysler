package com.identifix.contentlabelingservice.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.databind.type.TypeFactory
import com.identifix.contentlabelingservice.label.LabelMakerMessage
import com.identifix.contentlabelingservice.label.chrysler.ChryslerServiceBulletinMessage
import com.identifix.contentlabelingservice.label.chrysler.ServiceBulletinLabelMaker
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SuppressWarnings('UnnecessaryGetter')
@RestController('/service-bulletins')
@Api(value = 'Label service bulletins for all vehicles')
class ServiceBulletinController {

    @Value('${content-labeling-service.support-service-uri}')
    String supportServiceUri

    ObjectMapper mapper = new ObjectMapper()

    private static final String ALL_SERVICE_BULLETIN_VEHICLE_TOC_NAMES = '/content/vehicleTocNames'

    @Autowired
    ServiceBulletinLabelMaker labelMaker

    @GetMapping('/label-all-vehicles')
    ResponseEntity<Map<String, List<String>>> labelAllVehicles() {
        String vehicleListString = getAllVehiclesText()
        List<VehicleDto> vehicles = parseVehicleList(vehicleListString)
        processVehicles(vehicles)
    }

    @GetMapping('/label-specific-vehicles')
    ResponseEntity<Map<String, List<String>>> labelSpecificVehicles(@RequestParam List<String> vehicles) {
        processVehicles(vehicles)
    }

    List<VehicleDto> parseVehicleList(String vehicleList) {
        CollectionType typeReference =
                TypeFactory.defaultInstance().constructCollectionType(List, VehicleDto)
        mapper.readValue(vehicleList, typeReference)
    }

    String getAllVehiclesText() {
        new URL("${supportServiceUri}${ALL_SERVICE_BULLETIN_VEHICLE_TOC_NAMES}").text
    }

    Map processVehicles(List<VehicleDto> vehicles) {
        Map results = ['processedTocs':[], failedTocs:[]]
        vehicles.each { vehicle ->
            LabelMakerMessage message = new ChryslerServiceBulletinMessage(vehicle)
            try {
                labelMaker.labelContent(message)
                results.processedTocs.add(vehicle.tocName)
            } catch (ignored) {
                results.failedTocs.add(vehicle.tocName)
            }
        }
        results
    }
}

class VehicleDto {
    String year, model, make, tocName, platform
}
