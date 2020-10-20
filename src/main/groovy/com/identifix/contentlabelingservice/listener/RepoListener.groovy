package com.identifix.contentlabelingservice.listener

import com.identifix.contentlabelingservice.service.GitService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class RepoListener {
    @Autowired
    GitService gitService

    @SuppressWarnings('UnusedMethodParameter')
    @EventListener
    void appReady(ApplicationReadyEvent event) {
        gitService.updateRepo()
    }
}
