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

    /**
     * Clones the remote git repository into the specified local directory
     */
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

    /**
     * Removes all files from within the local git repository
     */
    fun removeAllFiles() {
        logger.debug { "Removing all files..." }

        localGitDirectory.walkTopDown().forEach {
            val relativeDirectory = it.relativeTo(localGitDirectory)

            if (!relativeDirectory.startsWith(".git") && relativeDirectory.toString().isNotEmpty()) {
                logger.trace { "Removing subdirectory '$relativeDirectory'..." }

                git.rm()
                    .addFilepattern(relativeDirectory.toString())
                    .call()

                relativeDirectory.delete()
            }
        }

        logger.debug { "Removed all files" }
    }

    /**
     * Adds all files in the local directory to the local git repository
     */
    fun addAllFiles() {
        logger.debug { "Adding all files..." }

        git.add()
            .addFilepattern(".")
            .call()

        logger.debug { "Added all files" }
    }

    /**
     * Commits the changes to the local repository
     */
    fun commit() {
        logger.debug { "Committing files..." }

        if (git.status().call().hasUncommittedChanges()) {
            val uncommittedChangesCount = git.status().call().uncommittedChanges.count()

            val commitCommand = git.commit()
                .setMessage("Committing $uncommittedChangesCount changed files by omnitracker-git")
                .setAuthor("omnitracker-git", "omnitrackergit@invalid.invalid")
                .call()

            logger.debug { "Committed $uncommittedChangesCount files" }
        } else {
            logger.debug { "Not committing, as there are no uncommitted changes." }
        }
    }

    /**
     * Pushes the local git repository to the remote git repository
     */
    fun push() {
        logger.debug { "Pushing to remote..." }

        val pushCommand =
            git.push()
                .setCredentialsProvider(credentialsProvider)
                .setForce(true)
                .setPushAll()

        try {
            val messages = pushCommand.call().joinToString(";") { it.messages }
            logger.debug { "Messages from push result: $messages" }
        } catch (e: InvalidRemoteException) {
            logger.error(e) { "Pushing to remote failed" }
        }
    }

    /**
     * Closes git
     */
    fun close() {
        logger.trace { "Closing..." }
        git.close()
        logger.trace { "Closed" }
    }

    /**
     * Deletes the local git repository/directory
     */
    fun deleteRepository() {
        logger.debug { "Deleting repository '${localGitDirectory.absolutePath}' (${localGitDirectory.recursiveFileCount} files)..." }
        val fullyDeleted = localGitDirectory.deleteRecursively()
        logger.debug { "Deleted repository '${localGitDirectory.absolutePath}'. Full deletion succeeded: $fullyDeleted; (${localGitDirectory.recursiveFileCount} files left)" }
    }
}
