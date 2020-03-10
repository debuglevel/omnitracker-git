package de.debuglevel.omnitrackergit.repository

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/repository")
@Tag(name = "repository")
class RepositoryController(private val repositoryService: RepositoryService) {
    private val logger = KotlinLogging.logger {}

    @Post("/")
    fun postOne(): HttpResponse<*> {
        logger.debug("Called postOne()")
        return try {
            repositoryService.commitScripts()

            HttpResponse.ok<Any>()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError<Any>()
        }
    }
}