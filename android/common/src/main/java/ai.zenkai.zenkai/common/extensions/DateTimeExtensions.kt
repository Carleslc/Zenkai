package ai.zenkai.zenkai.common.extensions

import ai.zenkai.zenkai.DateTime
import org.ocpsoft.prettytime.PrettyTime

private val PRETTY_TIME by lazy { PrettyTime() }

val DateTime.prettyDuration: String
    get() = PRETTY_TIME.formatDuration(toDate())

val DateTime.pretty: String
    get() = PRETTY_TIME.format(toDate())