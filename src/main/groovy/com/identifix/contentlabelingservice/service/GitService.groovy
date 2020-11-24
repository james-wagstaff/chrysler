package com.identifix.contentlabelingservice.service

import com.identifix.contentlabelingservice.configuration.LabelingServiceConfig
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

    void uploadCsv(String csv, String oem, String manualType, String filename) {
        filename = filename.replaceAll('/', '_')
        updateRepo(true)
        checkoutNewLabelBranch(filename)
        File file = new File("${labelingServiceConfig.gitDir}/_nuxeo_labeling/${oem}/To Do/${manualType}/${filename}.csv")
        file.createNewFile()
        PrintWriter writer = new PrintWriter(file)
        writer.print("")
        file.append(csv.toString().bytes)
        file.createNewFile()
        addAndPush("_nuxeo_labeling/${oem}/To Do/${manualType}/${filename}.csv", "Added ${filename}.csv")
        updateRepo(true)
    }

    void addAndPush(String filePattern, String commitMessage) {
        File gitDir = new File(labelingServiceConfig.gitDir)
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(labelingServiceConfig.repoUsername, labelingServiceConfig.repoAuthValue)
        if (gitDir.exists()) {
            log.info('Repo detected')
            Git git = Git.open(gitDir)
            git.add().addFilepattern(filePattern).call()
            git.commit().setMessage(commitMessage).call()
            git.push().setCredentialsProvider(credentialsProvider).call()
        } else {
            throw new Exception("Repo does not exist")
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

    void checkoutNewLabelBranch(String branchName) {
        File gitDir = new File(labelingServiceConfig.gitDir)
        if (gitDir.exists()) {
            log.info('Repo detected')
            Git git = Git.open(gitDir)
            List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
            branches = branches.findAll {it.name.contains('remotes') && it.name.contains(branchName.replaceAll(" ", "_"))}
            git.checkout().
                    setName("${branchName.replaceAll(' ', '_')}${branches.size() == 0 ? '': "-${findLastIndexOfBranch(branches, branchName.replaceAll(" ", "_"))}"}").
                    setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).
                    setStartPoint("origin/master").
                    setCreateBranch(true)
                    .call()
        } else {
            throw new Exception("Repo does not exist")
        }
    }

    static int findLastIndexOfBranch(List<Ref> branches, String branchName) {
        int value = 0
        branches.findAll{!it.name.endsWith(branchName) }.each {
            int nameValue = it.name.split('-').last().toInteger()
            if (nameValue > value) {
                value = nameValue
            }
        }
        value + 1
    }
}
