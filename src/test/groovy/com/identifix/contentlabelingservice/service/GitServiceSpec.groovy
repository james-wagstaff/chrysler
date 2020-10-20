package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import spock.lang.Specification

class GitServiceSpec extends Specification {

    GitService systemUnderTest = new GitService()
    LabelingServiceConfig labelingServiceConfig = new LabelingServiceConfig().with {
        repoUsername = 'test'
        repoAuthValue = 'test'
        repoUrl = 'test'
        gitDir = 'src/test/resources/repo'
        it
    }

    void setup() {
        systemUnderTest.labelingServiceConfig = labelingServiceConfig
    }

    def 'git repo is local'() {
        given:
            def buffer = new ByteArrayOutputStream()
            System.out = new PrintStream(buffer)
        when:
            systemUnderTest.updateRepo()
        then:
            buffer.toString().contains('Repo detected')
            Exception exception = thrown()
            exception.message.contains('repository not found')
    }

    def 'git repo is local and rules exists'() {
        def gitService = Spy(GitService) {
            updateRepo() >> null
            it.labelingServiceConfig = labelingServiceConfig
        }
        when:
            String actual = gitService.getBaseRules('ford', 'workshop')
        then:
            actual == 'PID,IDPROC-Test\n'
    }

    def 'git repo is local and rules do not exist'() {
        def gitService = Spy(GitService) {
            updateRepo() >> null
            it.labelingServiceConfig = labelingServiceConfig
        }
        when:
            String actual = gitService.getBaseRules('test', 'test')
        then:
            actual == ''
    }

    def 'git repo needs cloning'() {
        given:
            def buffer = new ByteArrayOutputStream()
            System.out = new PrintStream(buffer)
        when:
            systemUnderTest.labelingServiceConfig.gitDir = 'test'
            systemUnderTest.updateRepo()
        then:
            buffer.toString().contains('Repo not detected cloning')
            Exception exception = thrown()
            exception.message.contains('Invalid remote: origin')
    }
}
