package com.identifix.contentlabelingservice.configuration

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
}
