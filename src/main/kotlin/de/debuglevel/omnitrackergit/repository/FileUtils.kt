package de.debuglevel.omnitrackergit.repository

import java.io.File
import java.nio.file.Files

val File.recursiveFileCount: Long
    get() {
        return Files
            .walk(this.toPath())
            .parallel()
            .filter { p -> !p.toFile().isDirectory }
            .count()
    }