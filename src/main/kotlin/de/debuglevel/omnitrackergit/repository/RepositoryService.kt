package de.debuglevel.omnitrackergit.repository

import de.debuglevel.omnitrackergit.script.ScriptService
import de.debuglevel.omnitrackergit.script.ScriptWriter
import io.micronaut.context.annotation.Property
import mu.KotlinLogging
import javax.inject.Singleton

@Singleton
class RepositoryService(
    @Property(name = "app.omnitrackergit.git.repository.uri") val gitRepositoryUri: String,
    @Property(name = "app.omnitrackergit.git.repository.user") val gitUser: String,
    @Property(name = "app.omnitrackergit.git.repository.password") val gitPassword: String,
    private val scriptWriter: ScriptWriter,
    private val scriptService: ScriptService
) {
    private val logger = KotlinLogging.logger {}

    fun commitScripts() {
        logger.debug { "Committing scripts..." }

        val scripts = scriptService.list()

        val git = GitRepository(
            gitRepositoryUri,
            gitUser,
            gitPassword
        )

        val localGitDirectory = createTempDir("omnitracker-git").toPath()

        git.clone(localGitDirectory)
        git.removeAll()
        scriptWriter.writeFiles(scripts, localGitDirectory)
        git.addAll()
        git.commit()
        git.push()
        git.close()
        git.delete()

        logger.debug { "Committed scripts" }
    }
}