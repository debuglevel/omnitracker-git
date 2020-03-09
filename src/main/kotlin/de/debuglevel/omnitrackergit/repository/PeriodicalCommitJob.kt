package de.debuglevel.omnitrackergit.repository

import io.micronaut.context.annotation.Property
import io.micronaut.scheduling.annotation.Scheduled
import mu.KotlinLogging
import javax.inject.Singleton

@Singleton
class PeriodicalCommitJob(
    @Property(name = "app.omnitrackergit.periodical-commit.enabled") val periodicalCommitEnabled: Boolean,
    private val repositoryService: RepositoryService
) {
    private val logger = KotlinLogging.logger {}

    @Scheduled(
        fixedDelay = "\${app.omnitrackergit.periodical-commit.interval}",
        initialDelay = "\${app.omnitrackergit.periodical-commit.interval}"
    )
    fun commitScripts() {
        if (periodicalCommitEnabled) {
            logger.debug { "Periodically committing scripts..." }
            repositoryService.commitScripts()
            logger.debug { "Periodically committed scripts" }
        }
    }
}