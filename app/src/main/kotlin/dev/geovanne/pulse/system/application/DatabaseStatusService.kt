package dev.geovanne.pulse.system.application

import dev.geovanne.pulse.system.domain.DatabaseStatus
import dev.geovanne.pulse.system.domain.DatabaseStatusSource
import org.springframework.stereotype.Service

@Service
class DatabaseStatusService(
    private val databaseStatusSource: DatabaseStatusSource,
) {
    fun current(): DatabaseStatus = databaseStatusSource.current()
}
