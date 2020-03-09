package de.debuglevel.omnitrackergit

import mu.KotlinLogging
import java.util.*

object EnvironmentUtils {
    private val logger = KotlinLogging.logger {}

    /**
     * Add a new environment variable to the environment of the JVM
     */
    fun addEnvironmentVariable(key: String, value: String)
    {
        logger.debug { "Setting environment variable '$key'='$value'..." }
        val env = System.getenv().toMutableMap()
        env[key] = value
        setEnv(env)
        logger.debug { "Set environment variable '$key'='$value'" }
    }

    /**
     * Sets an environment variable inside the JVM
     *
     * Pure evil insane code from https://stackoverflow.com/a/7201825/4764279
     */
    private fun setEnv(newenv: Map<String, String>) {
        try {
            val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
            val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
            theEnvironmentField.isAccessible = true
            val env = theEnvironmentField.get(null) as MutableMap<String, String>
            env.putAll(newenv)
            val theCaseInsensitiveEnvironmentField =
                processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
            theCaseInsensitiveEnvironmentField.isAccessible = true
            val cienv = theCaseInsensitiveEnvironmentField.get(null) as MutableMap<String, String>
            cienv.putAll(newenv)
        } catch (e: NoSuchFieldException) {
            val classes = Collections::class.java.declaredClasses
            val env = System.getenv()
            for (cl in classes) {
                if ("java.util.Collections\$UnmodifiableMap" == cl.name) {
                    val field = cl.getDeclaredField("m")
                    field.isAccessible = true
                    val obj = field.get(env)
                    val map = obj as MutableMap<String, String>
                    map.clear()
                    map.putAll(newenv)
                }
            }
        }

    }
}