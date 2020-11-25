package com.identifix.contentlabelingservice.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class LabelingServiceConfig {
    @Value('${content-labeling-service.repo-user-name}')
    String repoUsername
    @Value('${content-labeling-service.repo-auth-value}')
    String repoAuthValue
    @Value('${content-labeling-service.repo-url}')
    String repoUrl
    String gitDir = 'repo'
    @Value('${content-labeling-service.generate-label-queue-name}')
    String generateQueueName
    @Value('${content-labeling-service.generate-label-exchange-name}')
    String generateExchangeName
    @Value('${content-labeling-service.complete-exchange-name}')
    String completeExchangeName

    @Autowired
    @Delegate
    KrakenClientConfiguration krakenClientConfiguration

    @Autowired
    @Delegate
    KrakenUriConfiguration krakenUriConfiguration
}
