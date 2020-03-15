package de.debuglevel.omnitrackergit.repository

import de.debuglevel.omnitrackergit.script.ScriptService
import de.debuglevel.omnitrackergit.script.ScriptWriter
import io.micronaut.context.annotation.Property
import mu.KotlinLogging
import javax.inject.Singleton

@Singleton
class RepositoryService(
    @Property(name = "app.omnitrackergit.git.repository.uri") val gitUri: String,
    @Property(name = "app.omnitrackergit.git.repository.branch") val gitBranch: String,
    @Property(name = "app.omnitrackergit.git.repository.user") val gitUser: String,
    @Property(name = "app.omnitrackergit.git.repository.password") val gitPassword: String,
    private val scriptWriter: ScriptWriter,
    private val scriptService: ScriptService
) {
    private val logger = KotlinLogging.logger {}

    fun commitScripts() {
        logger.debug { "Committing scripts..." }

        val scripts = scriptService.getScripts()

        val temporaryGitDirectory = createTempDir("omnitracker-git").toPath()

        val git = GitRepository(
            gitUri,
            gitBranch,
            gitUser,
            gitPassword,
            temporaryGitDirectory
        )

        try {
            git.clone()
            git.removeAllFiles()
            scriptWriter.writeFiles(scripts, temporaryGitDirectory)
            git.addAllFiles()
            git.commit()
            git.push()
        } catch (e: Exception) {
            logger.error(e) { "Something failed during committing scripts" }
        } finally {
            git.close()
            git.deleteRepository()
        }

        logger.debug { "Committed scripts" }
    }
}