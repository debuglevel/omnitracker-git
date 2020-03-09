package de.debuglevel.omnitrackergit.repository

import io.micronaut.context.annotation.Property
import io.micronaut.scheduling.annotation.Scheduled
import mu.KotlinLogging
import javax.inject.Singleton

@Singleton
class CommitJob(
    @Property(name = "app.omnitrackergit.scheduled-commits.enabled") val scheduledCommitsEnabled: Boolean,
    private val repositoryService: RepositoryService
) {
    private val logger = KotlinLogging.logger {}

    @Scheduled(
        fixedDelay = "\${app.omnitrackergit.periodical-commit.interval}",
        initialDelay = "\${app.omnitrackergit.periodical-commit.interval}"
    )
    fun commitScripts() {
        if (scheduledCommitsEnabled) {
            logger.debug { "Periodically committing scripts..." }
            repositoryService.commitScripts()
            logger.debug { "Periodically committed scripts" }
        }
    }
}