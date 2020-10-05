package de.debuglevel.omnitrackergit.layout

import de.debuglevel.omnitrackerdatabasebinding.layout.Layout
import de.debuglevel.omnitrackerdatabasebinding.layout.LayoutService
import mu.KotlinLogging
import javax.inject.Singleton

@Singleton
class LayoutService(
    private val layoutService: LayoutService
) {
    private val logger = KotlinLogging.logger {}

    fun getLayouts(): Set<Layout> {
        logger.debug { "Getting all layouts..." }

        layoutService.clearCache()
        val layouts = layoutService.getAll().values

        logger.debug { "Got ${layouts.size} layouts" }
        return layouts.toSet()
    }
}