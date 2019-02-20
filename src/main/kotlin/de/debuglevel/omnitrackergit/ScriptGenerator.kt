package de.debuglevel.omnitrackergit

import de.debuglevel.omnitrackerdatabasebinding.models.Script
import mu.KotlinLogging
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ScriptGenerator(private val baseDirectory: Path) {
    private val logger = KotlinLogging.logger {}

    fun writeFiles(scripts: Collection<Script>) {
        logger.debug("Generating script files...")

        scripts.forEach { writeFile(it) }
    }

    private fun buildFilePath(script: Script): Path {
        logger.debug("Building file name for $script...")

        // generates something like: "123 MyFolder"
        val directoryPath = "${script.folder?.id} ${script.folder?.alias}"

        // generates something like: "456 17 MyScript"
        val sanitizedName = script.name
            .replace('\\', '_')
            .replace('/', '_')
            .replace(':', '_')
            .replace('*', '_')
            .replace('?', '_')
            .replace('"', '_')
            .replace('<', '_')
            .replace('>', '_')
            .replace('|', '_')
            .replace('"', '_')
        val scriptFilename = "${script.id} ${script.type?.id} $sanitizedName"

        // generates something like: "123 MyFolder/456 17 MyScript"
        val scriptPath = Paths.get(directoryPath, scriptFilename)

        logger.debug("Built file path for $script: $scriptPath")

        return scriptPath
    }

    private fun createDirectory(scriptDirectory: Path) {
        //val fullDirectory = baseDirectory.resolve(scriptDirectory).toPath()
        logger.debug { "Creating directory (if not already exists) '$scriptDirectory'" }
        Files.createDirectories(scriptDirectory)
        //return fullDirectory
    }

    private fun writeFile(script: Script) {
        logger.debug("Writing script '${script.folder?.path}\\${script.name}'...")
        val scriptFilePath = buildFilePath(script)

        var content =
            """
                    ' File: $scriptFilePath
                    ' ID: ${script.id}
                    ' Name: ${script.name}
                    ' Type: ${script.type}
                    ' Folder: ${script.folder?.path}
                    ' ====================================================

                """.trimIndent()
        content += script.content

        createDirectory(baseDirectory.resolve(scriptFilePath.parent))
        val scriptRepositoryPath = baseDirectory.resolve(scriptFilePath)

        logger.debug("Writing script to file '$scriptRepositoryPath'...")

        val writer = object : PrintWriter(scriptRepositoryPath.toFile(), StandardCharsets.UTF_8.name()) {
            override fun println() {
                write("\n")
            }
        }

        writer.append(content)
        writer.close()
    }
}