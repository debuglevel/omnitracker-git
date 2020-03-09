package de.debuglevel.omnitrackergit.repository

import mu.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.nio.file.Path

class GitRepository(
    private val repositoryUri: String,
    username: String,
    password: String,
    directory: Path
) {
    private val logger = KotlinLogging.logger {}

    private val credentialsProvider = UsernamePasswordCredentialsProvider(username, password)
    private var localGitDirectory = directory.toFile()
    private lateinit var git: Git

    fun clone() {
        logger.debug { "Cloning '$repositoryUri' to '${localGitDirectory.absolutePath}'..." }

        git = Git
            .cloneRepository()
            .setURI(repositoryUri)
            .setCredentialsProvider(credentialsProvider)
            .setDirectory(localGitDirectory)
            .call()

        logger.debug { "Cloned '$repositoryUri' to '${localGitDirectory.absolutePath}'" }
    }

    fun addAll() {
        logger.debug { "Adding all files..." }

        git.add()
            .addFilepattern(".")
            .call()

        logger.debug { "Added all files" }
    }

    fun commit() {
        logger.debug { "Committing files..." }

        if (git.status().call().hasUncommittedChanges()) {
            val uncommittedChangesCound = git.status().call().uncommittedChanges.count()

            val commitCommand = git.commit()
                .setMessage("Committing $uncommittedChangesCound changed files by omnitracker-git")
                .setAuthor("omnitracker-git", "omnitrackergit@invalid.invalid")
                .call()

            logger.debug { "Committed files" }
        } else {
            logger.debug { "Not committing, as there are no uncommitted changes." }
        }
    }

    fun push() {
        logger.debug { "Pushing to remote..." }

        val pushCommand =
            git.push()
                .setCredentialsProvider(credentialsProvider)
                .setForce(true)
                .setPushAll()

        try {
            val messages = pushCommand.call().joinToString(";") { it.messages }
            logger.trace { "Messages from push result: $messages" }
        } catch (e: InvalidRemoteException) {
            logger.error(e) { "Pushing to remote failed" }
        }
    }

    fun delete() {
        logger.debug { "Deleting repository (${localGitDirectory.recursiveFileCount} files)..." }
        val fullyDeleted = localGitDirectory.deleteRecursively()
        logger.debug { "Deleted repository. Full deletion succeeded: $fullyDeleted; (${localGitDirectory.recursiveFileCount} files left)" }
    }

    fun removeAll() {
        logger.debug { "Removing all files..." }

        localGitDirectory.walkTopDown().forEach {
            val relativeDirectory = it.relativeTo(localGitDirectory)

            if (!relativeDirectory.startsWith(".git") && relativeDirectory.toString().isNotEmpty()) {
                logger.trace { "Removing $relativeDirectory..." }

                git.rm()
                    .addFilepattern(relativeDirectory.toString())
                    .call()

                relativeDirectory.delete()
            }
        }

        logger.debug { "Removed all files" }
    }

    fun close() {
        logger.trace { "Closing..." }
        git.close()
        logger.trace { "Closed" }
    }
}
