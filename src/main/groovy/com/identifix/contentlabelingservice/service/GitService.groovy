package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
import com.identifix.contentlabelingservice.error.BitBucketNetworkException
import groovy.util.logging.Slf4j
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Ref
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
        updateRepo(true)
        checkoutNewLabelBranch(cleanFilename)
        File file = new File("${labelingServiceConfig.gitDir}/${path}")
        file.createNewFile()
        PrintWriter writer = new PrintWriter(file)
        writer.print(BLANK)
        file.append(csv.toString().bytes)
        file.createNewFile()
        addAndPush(path, "Added ${cleanFilename}.csv")
        updateRepo(true)
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

    void checkoutNewLabelBranch(String branchName) {
        File gitDir = new File(labelingServiceConfig.gitDir)
        if (gitDir.exists()) {
            log.info(REPO_DETECTED)
            Git git = openGit(gitDir)
            List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
            branches = branches.findAll { it.name.contains('remotes') && it.name.contains(branchName.replaceAll(SPACE, UNDERSCORE)) }
            git.checkout().
                    setName("${ branchName.replaceAll(SPACE, UNDERSCORE) }${ branches.size() == 0 ? BLANK : "-${ findLastIndexOfBranch(branches, branchName.replaceAll(SPACE, UNDERSCORE)) }" }").
                    setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).
                    setStartPoint("origin/master").
                    setCreateBranch(true)
                    .call()
        } else {
            throw new BitBucketNetworkException(REPO_DOES_NOT_EXIST)
        }
    }

    static int findLastIndexOfBranch(List<Ref> branches, String branchName) {
        int value = 0
        branches.findAll { !it.name.endsWith(branchName) }.each {
            int nameValue = it.name.split('-').last().toInteger()
            if (nameValue > value) {
                value = nameValue
            }
        }
        value + 1
    }
}
