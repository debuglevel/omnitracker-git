package de.debuglevel.omnitrackergit.script

import de.debuglevel.omnitrackergit.layout.LayoutResponse
import de.debuglevel.omnitrackergit.layout.LayoutService
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
class ScriptController(private val layoutService: LayoutService) {
    private val logger = KotlinLogging.logger {}

    @Get("/")
    fun getAll(): HttpResponse<Set<LayoutResponse>> {
        logger.debug("Called getAll()")
        return try {
            val scripts = layoutService.getLayouts()
            val scriptsResponse = scripts
                .map { LayoutResponse(it) }
                .toSet()

            HttpResponse.ok(scriptsResponse)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError<Set<LayoutResponse>>()
        }
    }
}