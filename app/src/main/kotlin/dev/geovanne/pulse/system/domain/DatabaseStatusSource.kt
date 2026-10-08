package dev.geovanne.pulse.system.domain

fun interface DatabaseStatusSource {
    fun current(): DatabaseStatus
}
