package de.debuglevel.omnitrackergit.script

import de.debuglevel.omnitrackerdatabasebinding.OmnitrackerDatabase
import de.debuglevel.omnitrackerdatabasebinding.script.Script
import mu.KotlinLogging
import javax.inject.Singleton

@Singleton
class ScriptService(
    private val omnitrackerDatabase: OmnitrackerDatabase
) {
    private val logger = KotlinLogging.logger {}

    fun getScripts(): Set<Script> {
        logger.debug { "Getting all scripts..." }

        val scripts = omnitrackerDatabase.scripts.values

        logger.debug { "Got ${scripts.size} scripts" }
        return scripts.toSet()
    }
}