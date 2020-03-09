package de.debuglevel.omnitrackergit.script

import de.debuglevel.omnitrackerdatabasebinding.models.Script
import java.util.*

data class ScriptResponse(
    var id: Int,
    var path: String
) {
    constructor(script: Script) : this(
        script.id,
        "${script.folder?.path}\\${script.name}"
    )
}