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
        logger.debug { "Writing script files..." }

        scripts.forEach { writeFile(it) }
    }

    private fun writeFile(script: Script) {
        val scriptPath = "${script.folder?.path}" + "\\" + script.name
        logger.trace { "Writing script '$scriptPath'..." }
        val scriptFilePath = buildFilePath(script)

        var content =
            """
                    ' File: ${scriptFilePath.joinToString("/")}
                    ' ID: ${script.id}
                    ' Name: ${script.name}
                    ' Type: ${script.type?.id}
                    ' Folder: ${script.folder?.name ?: "no folder"}
                    ' ====================================================

                """.trimIndent()
        content += script.content

        createDirectory(baseDirectory.resolve(scriptFilePath.parent))
        val scriptRepositoryPath = baseDirectory.resolve(scriptFilePath)

        logger.debug { "Writing script '${script.folder?.path}\\\${script.name}' to file '$scriptRepositoryPath'..." }

        val writer = object : PrintWriter(scriptRepositoryPath.toFile(), StandardCharsets.UTF_8.name()) {
            override fun println() {
                write("\n")
            }
        }

        writer.append(content)
        writer.close()
    }

    private fun buildFilePath(script: Script): Path {
        logger.trace { "Building file name for $script..." }

        // generates something like: "123 MyFolder"
        val directoryPath = if (script.folder != null) {
            if (script.folder?.alias == null) {
                "${script.folder?.id} no alias"
            } else {
                "${script.folder?.id} ${script.folder?.alias}"
            }
        } else {
            "-1 no folder"
        }

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

        logger.trace { "Built file path for $script: $scriptPath" }

        return scriptPath
    }

    private fun createDirectory(scriptDirectory: Path) {
        logger.trace { "Creating directory (if not already exists) '$scriptDirectory'" }
        Files.createDirectories(scriptDirectory)
    }
}