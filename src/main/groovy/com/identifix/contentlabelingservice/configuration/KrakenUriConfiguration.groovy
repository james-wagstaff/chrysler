package com.identifix.contentlabelingservice.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KrakenUriConfiguration {

    @Value('${content-labeling-service.kraken-uri}')
    String krakenUri

    @Value('${content-labeling-service.security-service-uri}')
    String securityServiceUri

}
