package com.identifix.contentlabelingservice.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class LabelingServiceConfig {
    @Value('${content-labeling-service.repo-user-name:michael.moseng@identifix.com}')
    String repoUsername
    @Value('${content-labeling-service.repo-auth-value:Nzk1NjI1ODQyMTU4Osvw7mOICz0K3mdccZFyU/1s2Fx7}')
    String repoAuthValue
    @Value('${content-labeling-service.repo-url:https://bitbucket.audatex.com/scm/srmic/oem-base-rules.git}')
    String repoUrl
    String gitDir = 'repo'
}
