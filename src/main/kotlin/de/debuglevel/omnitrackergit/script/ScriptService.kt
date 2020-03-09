package de.debuglevel.omnitrackergit.script

import de.debuglevel.omnitrackerdatabasebinding.OmnitrackerDatabase
import de.debuglevel.omnitrackerdatabasebinding.models.Script
import de.debuglevel.omnitrackergit.EnvironmentUtils
import io.micronaut.context.annotation.Property
import mu.KotlinLogging
import java.util.*
import javax.inject.Singleton
import java.lang.reflect.AccessibleObject.setAccessible

@Singleton
class ScriptService(
    @Property(name = "app.omnitrackergit.database.connectionstring") val connectionString: String
    ) {
    private val logger = KotlinLogging.logger {}

    init {
        // the underlying library relies on a configuration.properties file or environment variables (with fixed keys)
        // as a work around, set a new environment variable with our connection string
        EnvironmentUtils.addEnvironmentVariable("DATABASE_CONNECTION_STRING", connectionString)
    }

    fun list(): Set<Script> {
        logger.debug { "Getting all scripts..." }

        val scripts = OmnitrackerDatabase().scripts.values

        logger.debug { "Got ${scripts.size} scripts" }
        return scripts.toSet()
    }
}