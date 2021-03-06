package de.debuglevel.omnitrackergit.script

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/scripts")
@Tag(name = "scripts")
class ScriptController(private val scriptService: ScriptService) {
    private val logger = KotlinLogging.logger {}

    @Get("/")
    fun getAll(): HttpResponse<Set<ScriptResponse>> {
        logger.debug("Called getAll()")
        return try {
            val scripts = scriptService.getScripts()
            val scriptsResponse = scripts
                .map { ScriptResponse(it) }
                .toSet()

            HttpResponse.ok(scriptsResponse)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError<Set<ScriptResponse>>()
        }
    }
}