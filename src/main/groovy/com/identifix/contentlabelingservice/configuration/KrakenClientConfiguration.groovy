package com.identifix.contentlabelingservice.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KrakenClientConfiguration {

    @Value('${client_id}')
    String clientId

    @Value('${client_secret}')
    String clientSecret
}
