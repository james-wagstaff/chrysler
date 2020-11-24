package com.identifix.contentlabelingservice.configuration

import spock.lang.Specification

class KrakenClientConfigurationSpec extends Specification {
    def 'check if vars are there'() {
        when:
            KrakenClientConfiguration test = new KrakenClientConfiguration().with {
                clientId = "test clientId"
                clientSecret = "test clientSecret"
                it
            }
        then:
            test.clientId == "test clientId"
            test.clientSecret == "test clientSecret"
    }
}
