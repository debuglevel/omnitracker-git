package de.debuglevel.omnitrackergit.layout

import de.debuglevel.omnitrackerdatabasebinding.layout.Layout

data class LayoutResponse(
    var id: Int,
    var path: String
) {
    constructor(layout: Layout) : this(
        layout.id,
        "${layout.folder.path}\\${layout.name}"
    )
}