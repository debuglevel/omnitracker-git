package de.debuglevel.omnitrackergit.repository

import de.debuglevel.omnitrackergit.script.ScriptService
import de.debuglevel.omnitrackergit.script.ScriptWriter
import io.micronaut.context.annotation.Property
import mu.KotlinLogging
import javax.inject.Singleton

@Singleton
class RepositoryService(
    @Property(name = "app.omnitrackergit.git.repository.uri") val gitUri: String,
    @Property(name = "app.omnitrackergit.git.repository.user") val gitUser: String,
    @Property(name = "app.omnitrackergit.git.repository.password") val gitPassword: String,
    private val scriptWriter: ScriptWriter,
    private val scriptService: ScriptService
) {
    private val logger = KotlinLogging.logger {}

    fun commitScripts() {
        logger.debug { "Committing scripts..." }

        val scripts = scriptService.getScripts()

        val git = GitRepository(
            gitUri,
            gitUser,
            gitPassword
        )

        val temporaryGitDirectory = createTempDir("omnitracker-git").toPath()

        git.clone(temporaryGitDirectory)
        git.removeAll()
        scriptWriter.writeFiles(scripts, temporaryGitDirectory)
        git.addAll()
        git.commit()
        git.push()
        git.close()
        git.delete()

        logger.debug { "Committed scripts" }
    }
}