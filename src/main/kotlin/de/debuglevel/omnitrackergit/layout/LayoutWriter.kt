package de.debuglevel.omnitrackergit.layout

import de.debuglevel.omnitrackerdatabasebinding.layout.Layout
import mu.KotlinLogging
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Singleton

@Singleton
class LayoutWriter {
    private val logger = KotlinLogging.logger {}

    fun writeFiles(scripts: Collection<Layout>, baseDirectory: Path) {
        logger.debug { "Writing layout files..." }
        scripts.forEach { writeFile(it, baseDirectory) }
        logger.debug { "Wrote layout files" }
    }

    private fun writeFile(layout: Layout, baseDirectory: Path) {
        val layoutPath = "${layout.folder.path}\\${layout.name}"
        logger.trace { "Writing layout '$layoutPath'..." }
        val layoutFilePath = buildFilePath(layout)

        var content =
            """
                    ' File: ${layoutFilePath.joinToString("/")}
                    ' ID: ${layout.id}
                    ' Name: ${layout.name}
                    ' Type: ${layout.type?.id}
                    ' Folder: ${layout.folder.name}
                    ' ====================================================

                """.trimIndent()
        content += layout.reportDataBase64

        createDirectory(baseDirectory.resolve(layoutFilePath.parent))
        val scriptRepositoryPath = baseDirectory.resolve(layoutFilePath)

        logger.trace { "Writing script '${layout.folder.path}\\\${script.name}' to file '$scriptRepositoryPath'..." }

        val writer = object : PrintWriter(scriptRepositoryPath.toFile(), StandardCharsets.UTF_8.name()) {
            override fun println() {
                write("\n")
            }
        }

        writer.append(content)
        writer.close()

        logger.trace { "Wrote script '$layoutPath'" }
    }

    private fun sanitizeFilename(filename: String?): String? {
        return filename
            ?.replace('\\', '_')
            ?.replace('/', '_')
            ?.replace(':', '_')
            ?.replace('*', '_')
            ?.replace('?', '_')
            ?.replace('"', '_')
            ?.replace('<', '_')
            ?.replace('>', '_')
            ?.replace('|', '_')
            ?.replace('"', '_')
            ?.replace('\u0000', '_')
    }

    private fun buildFilePath(layout: Layout): Path {
        logger.trace { "Building file path for $layout..." }

        // generates something like: "123 MyFolder"
        val directoryPath = if (layout.folder.alias == null) {
            "${layout.folder.id} no alias"
        } else {
            val sanitizedFolderAlias = sanitizeFilename(layout.folder.alias)
            "${layout.folder.id} $sanitizedFolderAlias"
        }

        // generates something like: "456 17 MyLayout"
        val sanitizedName = sanitizeFilename(layout.name)
        val layoutFilename = "${layout.id} ${layout.type?.id} $sanitizedName"

        // generates something like: "123 MyFolder/456 17 MyLayout"
        val layoutPath = Paths.get(directoryPath, layoutFilename)

        logger.trace { "Built file path for $layout: $layoutPath" }
        return layoutPath
    }

    private fun createDirectory(layoutDirectory: Path) {
        logger.trace { "Creating directory (if not already exists) '$layoutDirectory'" }
        Files.createDirectories(layoutDirectory)
        logger.trace { "Created directory (if not already exists) '$layoutDirectory'" }
    }
}