package com.identifix.contentlabelingservice.configuration

import spock.lang.Specification

class KrakenUriConfigurationSpec extends Specification {
    def 'check if vars are there'() {
        when:
            KrakenUriConfiguration test = new KrakenUriConfiguration().with {
                krakenUri = "test krakenUri"
                securityServiceUri = "test securityServiceUri"
                it
            }
        then:
            test.krakenUri == "test krakenUri"
            test.securityServiceUri == "test securityServiceUri"
    }
}
