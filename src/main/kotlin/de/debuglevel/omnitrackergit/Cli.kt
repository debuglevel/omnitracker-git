package de.debuglevel.omnitrackergit

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import de.debuglevel.omnitrackerdatabasebinding.OmnitrackerDatabase
import mu.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

class OmnitrackerGit : CliktCommand() {
    override fun run() = Unit
}

class Commit : CliktCommand(help = "Commit scripts to git repository") {
    override fun run() {
        updateRepository()
    }

    companion object {
        fun updateRepository() {
            val scripts = OmnitrackerDatabase().scripts.values

            val git = GitRepository(
                Configuration.gitRepositoryUri,
                Configuration.gitUser,
                Configuration.gitPassword
            )

            val localGitDirectory = createTempDir("omnitracker-git").toPath()

            val scriptGenerator = ScriptGenerator(localGitDirectory)

            git.clone(localGitDirectory)
            git.removeAll()
            scriptGenerator.writeFiles(scripts)
            git.addAll()
            git.commit()
            git.push()
            git.delete()
        }
    }
}

class Export : CliktCommand(help = "Export scripts into local directory") {
    private val destinationDirectory: Path by argument(
        "destination-directory",
        help = "The directory to save the scripts into"
    )
        .path(
            folderOkay = true,
            fileOkay = false,
            writable = true
        )

    override fun run() {
        exportScripts(destinationDirectory)
    }

    companion object {
        fun exportScripts(destinationDirectory: Path) {
            val scripts = OmnitrackerDatabase().scripts.values
            val scriptGenerator = ScriptGenerator(destinationDirectory)
            scriptGenerator.writeFiles(scripts)
        }
    }
}

class List : CliktCommand(help = "List all scripts") {
    override fun run() {
        logger.debug("Getting scripts from database...")
        OmnitrackerDatabase().scripts
            .values
            .sortedWith(compareBy({ it.folder?.name }, { it.name }))
            .forEach { println("${it.id}\t| ${it.folder?.path}\\${it.name}") }
    }
}

fun main(args: Array<String>) = OmnitrackerGit()
    .subcommands(List(), Export(), Commit())
    .main(args)