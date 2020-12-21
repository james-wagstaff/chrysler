package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.contentlabelingservice.error.BitBucketNetworkException
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

    static final REPO_DETECTED = 'Repo detected'
    static final REPO_DOES_NOT_EXIST = "Repo does not exist"
    static final BLANK = ""
    static final SPACE = " "
    static final UNDERSCORE = "_"

    String getBaseRules(String publisher, String manualType) {
        updateRepo()
        try {
            File file = new File("${labelingServiceConfig.gitDir}/${publisher.toLowerCase()}/${manualType.toLowerCase().replaceAll(SPACE, UNDERSCORE)}_base_rules.csv")
            file.text
        } catch (FileNotFoundException e) {
            log.error(e.message)
            BLANK
        }
    }

    void uploadCsv(String csv, String oem, String manualType, String filename) {
        String cleanFilename = filename.replaceAll('/', UNDERSCORE)
        String path = "_nuxeo_labeling/${oem}/To Do/${manualType}/${cleanFilename}.csv"
        updateRepo(false)
        File file = new File("${labelingServiceConfig.gitDir}/${path}")
        file.createNewFile()
        PrintWriter writer = new PrintWriter(file)
        writer.print(BLANK)
        file.append(csv.toString().bytes)
        file.createNewFile()
        addAndPush(path, "Added ${cleanFilename}.csv")
    }

    void addAndPush(String filePattern, String commitMessage) {
        File gitDir = new File(labelingServiceConfig.gitDir)
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(labelingServiceConfig.repoUsername, labelingServiceConfig.repoAuthValue)
        if (gitDir.exists()) {
            log.info(REPO_DETECTED)
            Git git = openGit(gitDir)
            git.add().addFilepattern(filePattern).call()
            git.commit().setMessage(commitMessage).call()
            git.push().setCredentialsProvider(credentialsProvider).call()
        } else {
            throw new BitBucketNetworkException(REPO_DOES_NOT_EXIST)
        }
    }

    void updateRepo() {
        updateRepo(false)
    }

    void updateRepo(boolean forceDownload) {
        File gitDir = new File(labelingServiceConfig.gitDir)
        if (forceDownload && gitDir.exists()) {
            gitDir.deleteDir()
        }
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(labelingServiceConfig.repoUsername, labelingServiceConfig.repoAuthValue)
        if (gitDir.exists()) {
            log.info(REPO_DETECTED)
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

    Git openGit(File gitDir) {
        Git.open(gitDir)
    }

    byte[] findCsv(String oem, String manualType, String id) {
        updateRepo()
        log.info("Finding File for ${oem} - ${manualType} - containing id: ${id}")
        String path = "_nuxeo_labeling/${oem}/Done/${manualType}"
        File labelDir = new File("${labelingServiceConfig.gitDir}/${path}")
        if (labelDir.exists() && labelDir.list().findAll { it.contains(id) }.size() == 1) {
            log.info("File Found for ${oem} - ${manualType} - containing id: ${id}")
            return new File("${labelDir.path}/${labelDir.list().findAll { it.contains(id) }.collect().first()}").text.bytes
        }
        log.info("File NOT Found for ${oem} - ${manualType} - containing id: ${id}")
        null
    }
}
