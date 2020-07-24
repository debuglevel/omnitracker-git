package de.debuglevel.omnitrackergit.layout

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/layouts")
@Tag(name = "layouts")
class LayoutController(private val layoutService: LayoutService) {
    private val logger = KotlinLogging.logger {}

    @Get("/")
    fun getAll(): HttpResponse<Set<LayoutResponse>> {
        logger.debug("Called getAll()")
        return try {
            val layouts = layoutService.getLayouts()
            val layoutsResponse = layouts
                .map { LayoutResponse(it) }
                .toSet()

            HttpResponse.ok(layoutsResponse)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError<Set<LayoutResponse>>()
        }
    }
}