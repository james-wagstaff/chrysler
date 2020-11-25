package com.identifix.contentlabelingservice.configuration

import spock.lang.Specification

class LabelingServiceConfigSpec extends Specification {
    def 'check if vars are there'() {
        when:
            LabelingServiceConfig test = new LabelingServiceConfig().with {
                repoUsername = "test repoUsername"
                completeExchangeName = "test completeExchangeName"
                repoAuthValue = "test repoAuthValue"
                repoUrl = "test repoUrl"
                generateQueueName = "test generateQueueName"
                generateExchangeName = "test generateExchangeName"
                krakenClientConfiguration = new KrakenClientConfiguration()
                krakenUriConfiguration = new KrakenUriConfiguration()
                it
            }
        then:
            test.repoUsername == "test repoUsername"
            test.completeExchangeName == "test completeExchangeName"
            test.repoAuthValue == "test repoAuthValue"
            test.generateExchangeName == "test generateExchangeName"
            test.repoUrl == "test repoUrl"
            test.generateQueueName == "test generateQueueName"
            test.gitDir == "repo"
            test.krakenClientConfiguration
            test.krakenUriConfiguration
    }
}
