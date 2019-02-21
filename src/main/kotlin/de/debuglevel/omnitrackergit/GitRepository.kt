package de.debuglevel.omnitrackergit

import mu.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.nio.file.Path

class GitRepository(
    private val repositoryUri: String,
    username: String,
    password: String
) {
    private val logger = KotlinLogging.logger {}

    private lateinit var localGitDirectory: File
    private val credentialsProvider = UsernamePasswordCredentialsProvider(username, password)
    private lateinit var git: Git

    fun clone(directory: Path) {
        logger.debug { "Cloning '$repositoryUri' to '${directory.toAbsolutePath()}'..." }

        this.localGitDirectory = directory.toFile()

        git = Git.cloneRepository()
            .setURI(repositoryUri)
            .setCredentialsProvider(credentialsProvider)
            .setDirectory(localGitDirectory)
            .call()
    }

    fun close() {
        logger.trace { "Closing..." }
        git.close()
    }

    fun delete() {
        logger.debug { "Deleting repository..." }
        localGitDirectory.deleteRecursively()
    }

    fun removeAll() {
        logger.debug { "Removing all files..." }

        localGitDirectory.walkTopDown().forEach {
            val relativeDirectory = it.relativeTo(localGitDirectory)

            if (!relativeDirectory.startsWith(".git") && !relativeDirectory.toString().isEmpty()) {
                logger.trace { "Removing $relativeDirectory..." }

                git.rm()
                    .addFilepattern(relativeDirectory.toString())
                    .call()

                relativeDirectory.delete()
            }
        }
    }

    fun addAll() {
        logger.debug { "Adding all files..." }

        git.add()
            .addFilepattern(".")
            .call()
    }

    fun commit() {
        logger.debug { "Committing files..." }

        if (git.status().call().hasUncommittedChanges()) {
            git.commit()
                .setMessage("Committing changed files by omnitracker-git")
                .setAuthor("omnitracker-git", "omnitrackergit@invalid.invalid")
                .call()
        } else {
            logger.debug { "Not committing, as there are no uncommitted changes." }
        }
    }

    fun push() {
        logger.debug { "Pushing to remote..." }

        val pushCommand = git.push()
            //.setCredentialsProvider(credentialsProvider) // TODO: might be unnecessary
            .setForce(true)
            .setPushAll()

        try {
            logger.trace { "Messages from push result:" }
            val it = pushCommand.call().iterator()
            if (it.hasNext()) {
                logger.trace { it.next().messages }
            }
        } catch (e: InvalidRemoteException) {
            logger.error(e) { "Pushing to remote failed" }
        }
    }
}