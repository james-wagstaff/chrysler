package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import groovy.util.logging.Slf4j
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class GitService {
    @Autowired
    LabelingServiceConfig labelingServiceConfig

    String getBaseRules(String publisher, String manualType) {
        updateRepo()
        try {
            File file = new File("${labelingServiceConfig.gitDir}/${publisher.toLowerCase()}/${manualType.toLowerCase().replaceAll(' ', '_')}_base_rules.csv")
            file.text
        } catch (FileNotFoundException e) {
            log.error(e.message)
            ''
        }
    }

    void updateRepo() {
        File gitDir = new File(labelingServiceConfig.gitDir)
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(labelingServiceConfig.repoUsername, labelingServiceConfig.repoAuthValue)
        if (gitDir.exists()) {
            log.info('Repo detected')
            Git git = Git.open(gitDir)
            git.pull().setCredentialsProvider(credentialsProvider).call()
        } else {
            log.info('Repo not detected cloning')
            CloneCommand clone = Git.cloneRepository()
            clone.setURI(labelingServiceConfig.repoUrl)
            clone.setDirectory(new File(labelingServiceConfig.gitDir))
            clone.setCredentialsProvider(credentialsProvider)
            clone.call()
        }
    }
}
