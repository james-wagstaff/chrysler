package com.identifix.contentlabelingservice.listener

import com.identifix.contentlabelingservice.service.GitService
import spock.lang.Specification

class RepoListenerSpec extends Specification {
    RepoListener systemUnderTest = new RepoListener()
    GitService gitService = Mock()

    void setup() {
        systemUnderTest.gitService = gitService
    }

    def 'test update or pull down'() {
        when:
            systemUnderTest.appReady(null)
        then:
            1 * gitService.updateRepo()
    }
}
