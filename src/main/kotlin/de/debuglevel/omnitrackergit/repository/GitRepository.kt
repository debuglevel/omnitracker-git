package de.debuglevel.omnitrackergit.repository

import mu.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevSort
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.IOException
import java.nio.file.Path


class GitRepository(
    private val repositoryUri: String,
    private val branch: String,
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
        logger.debug { "Cloning '$repositoryUri' branch '$branch' to '${localGitDirectory.absolutePath}'..." }

        git = Git
            .cloneRepository()
            .setURI(repositoryUri)
            .setCredentialsProvider(credentialsProvider)
            .setBranch(branch)
            .setDirectory(localGitDirectory)
            .call()

        createBranch()

        logger.debug { "Cloned '$repositoryUri' branch '$branch' to '${localGitDirectory.absolutePath}'" }
    }

    /**
     * Creates the remote branch if it does not exist already.
     * XXX: Probably creates the branch on the latest commit in "master" branch.
     */
    private fun createBranch() {
        logger.debug { "Creating branch '$branch' on '$repositoryUri' if not existing..." }

        if (!checkBranchExists()) {
            logger.debug { "Creating new branch '$branch'..." }
            // XXX: Don't know what actually happens here, but it works in contrast to many other variants.
            val ref = git.checkout()
                .setCreateBranch(true)
                .setName(branch)
                .setStartPoint("origin/master")
                .call()
            logger.debug { "Created new branch '$branch': $ref" }
        }
    }

    private fun getFirstCommit(): RevCommit {
        val revWalk = RevWalk(git.repository)
        try {

            val headId = git.repository.readOrigHead()

            //val headId = git.repository.resolve(Constants.HEAD)
            val root = revWalk.parseCommit(headId)
            revWalk.sort(RevSort.REVERSE)
            revWalk.markStart(root)
            val revCommit = revWalk.next()

            return revCommit
        } catch (e: IOException) {
            throw e
        }
    }

//    private fun checkBranchExists(): Boolean {
//        logger.debug { "Checking if branch '$branch' on '$repositoryUri' exists..." }
//        val refs = Git.lsRemoteRepository()
//            .setHeads(true)
//            .setRemote(repositoryUri)
//            .call()
//
//        val branchNames = refs.map { it.name.replace("refs/heads/", "") }
//        logger.trace { "Branches on remote: " + branchNames.joinToString { it }}
//
//        val isBranchExisting = branchNames.contains(branch)
//
//        logger.debug { "Checked if branch '$branch' on '$repositoryUri' exists: $isBranchExisting" }
//        return isBranchExisting
//    }

    private fun checkBranchExists(): Boolean {
        logger.debug { "Checking if branch '$branch' exists..." }
        val refs = git.branchList()
            .setListMode(ListBranchCommand.ListMode.ALL)
            .call()

        val branchNames = refs.map { it.name.replace("refs/heads/", "") }
        logger.trace { "Branches: " + branchNames.joinToString { it } }

        val isBranchExisting = branchNames.contains(branch)

        logger.debug { "Checked if branch '$branch' exists: $isBranchExisting" }
        return isBranchExisting
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
