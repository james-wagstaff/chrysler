package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.contentlabelingservice.error.BitBucketNetworkException
import org.eclipse.jgit.api.AddCommand
import org.eclipse.jgit.api.CommitCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PushCommand
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.StatusCommand
import spock.lang.Specification

class GitServiceSpec extends Specification {

    Set<String> fileChange = ["test"]
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

    def 'addAndPush no dir'() {
        systemUnderTest.labelingServiceConfig.gitDir = 'not found'
        when:
            systemUnderTest.addAndPush('', '')
        then:
            Exception exception = thrown()
            exception.message == 'Repo does not exist'
    }

    def 'open git'() {
        when:
            systemUnderTest.openGit(new File(labelingServiceConfig.gitDir))
        then:
            Exception exception = thrown()
            exception.message.contains('repository not found')
    }

    def 'upload csv'() {
        Git mockGit = Mock(Git)
        AddCommand mockAddCommand = Mock(AddCommand)
        CommitCommand mockCommitCommand = Mock(CommitCommand)
        PushCommand mockPushCommand = Mock(PushCommand)
        def gitService = Spy(GitService) {
            updateRepo(false) >> null
            openGit(_ as File) >> mockGit
            isDirty() >> false
            it.labelingServiceConfig = labelingServiceConfig
        }
        when:
            gitService.uploadCsv("", 'toyota', 'Repair Manual', 'test')
        then:
            1 * mockGit.add() >> mockAddCommand
            1 * mockAddCommand.addFilepattern(_) >> mockAddCommand
            1 * mockAddCommand.call()
            1 * mockGit.commit() >> mockCommitCommand
            1 * mockCommitCommand.setMessage(_) >> mockCommitCommand
            1 * mockCommitCommand.call()
            1 * mockGit.push() >> mockPushCommand
            1 * mockPushCommand.setCredentialsProvider(_) >> mockPushCommand
            1 * mockPushCommand.call()

    }

    def 'upload csv more then 1 file'() {
        Git mockGit = Mock(Git)
        def gitService = Spy(GitService) {
            updateRepo(false) >> null
            updateRepo(true) >> null
            openGit(_ as File) >> mockGit
            isDirty() >> true
            it.labelingServiceConfig = labelingServiceConfig
        }
        when:
            gitService.uploadCsv("", 'toyota', 'Repair Manual', 'test')
        then:
            0 * mockGit.add()
            thrown(BitBucketNetworkException)

    }

    def 'is dirty'() {
        Git mockGit = Mock(Git)
        StatusCommand statusCommand = Mock(StatusCommand)
        Status status = Mock(Status)
        def gitService = Spy(GitService) {
            openGit(_ as File) >> mockGit
            it.labelingServiceConfig = labelingServiceConfig
        }
        when:
            boolean actual = gitService.isDirty()
        then:
            1 * mockGit.status() >> statusCommand
            1 * statusCommand.call() >> status
            1 * status.removed >> fileChange
            1 * status.modified >> fileChange
            1 * status.added >> fileChange
            1 * status.changed >> fileChange
            1 * status.missing >> fileChange
            1 * status.untracked >> fileChange
            1 * status.ignoredNotInIndex >> fileChange
            1 * status.conflicting >> fileChange
            actual
    }

    def 'is not dirty'() {
        Git mockGit = Mock(Git)
        StatusCommand statusCommand = Mock(StatusCommand)
        Status status = Mock(Status)
        def gitService = Spy(GitService) {
            openGit(_ as File) >> mockGit
            it.labelingServiceConfig = labelingServiceConfig
        }
        when:
            boolean actual = gitService.isDirty()
        then:
            1 * mockGit.status() >> statusCommand
            1 * statusCommand.call() >> status
            1 * status.removed >> fileChange
            1 * status.modified >> []
            1 * status.added >> []
            1 * status.changed >> []
            1 * status.missing >> []
            1 * status.untracked >> []
            1 * status.ignoredNotInIndex >> []
            1 * status.conflicting >> []
            !actual
    }

    def 'get csv found'() {
        def gitService = Spy(GitService) {
            updateRepo() >> null
            it.labelingServiceConfig = labelingServiceConfig
        }
        when:
            byte[] actual = gitService.findCsv('toyota', 'Repair Manual', 'test')
        then:
            actual != null
    }

    def 'get csv not found'() {
        def gitService = Spy(GitService) {
            updateRepo() >> null
            it.labelingServiceConfig = labelingServiceConfig
        }
        when:
            byte[] actual = gitService.findCsv('Toyota', 'Repair Manual', 'fail')
        then:
            actual == null
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
