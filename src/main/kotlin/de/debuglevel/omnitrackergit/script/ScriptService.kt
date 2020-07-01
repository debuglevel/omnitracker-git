package de.debuglevel.omnitrackergit.script

import de.debuglevel.omnitrackerdatabasebinding.script.Script
import de.debuglevel.omnitrackerdatabasebinding.script.ScriptService
import mu.KotlinLogging
import javax.inject.Singleton

@Singleton
class ScriptService(
    private val scriptService: ScriptService
) {
    private val logger = KotlinLogging.logger {}

    fun getScripts(): Set<Script> {
        logger.debug { "Getting all scripts..." }

        scriptService.clearCache()
        val scripts = scriptService.getAll().values

        logger.debug { "Got ${scripts.size} scripts" }
        return scripts.toSet()
    }
}