package de.debuglevel.omnitrackergit.repository

import de.debuglevel.omnitrackergit.layout.LayoutService
import de.debuglevel.omnitrackergit.layout.LayoutWriter
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
    private val layoutWriter: LayoutWriter,
    private val scriptService: ScriptService,
    private val layoutService: LayoutService
) {
    private val logger = KotlinLogging.logger {}

    fun commitScripts() {
        logger.debug { "Committing scripts..." }

        val scripts = scriptService.getScripts()

        val temporaryGitDirectory = createTempDir("omnitracker-git").toPath()

        val scriptsGitBranch =
            gitBranch // TODO/NOTE: do not use a entity postfix (like for layouts) for backwards compatibility
        val git = GitRepository(
            gitUri,
            scriptsGitBranch,
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

    fun commitLayouts() {
        logger.debug { "Committing layouts..." }

        val layouts = layoutService.getLayouts()

        val temporaryGitDirectory = createTempDir("omnitracker-git").toPath()

        val layoutsGitBranch = "$gitBranch-layouts"
        val git = GitRepository(
            gitUri,
            layoutsGitBranch,
            gitUser,
            gitPassword,
            temporaryGitDirectory
        )

        try {
            git.clone()
            git.removeAllFiles()
            layoutWriter.writeFiles(layouts, temporaryGitDirectory)
            git.addAllFiles()
            git.commit()
            git.push()
        } catch (e: Exception) {
            logger.error(e) { "Something failed during committing layouts" }
        } finally {
            git.close()
            git.deleteRepository()
        }

        logger.debug { "Committed layouts" }
    }
}