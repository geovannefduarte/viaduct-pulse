package dev.geovanne.pulse.platform.database

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import java.nio.file.Path

fun startEmbeddedPostgres(dataDirectory: Path): EmbeddedPostgres =
    durableBuilder()
        .setDataDirectory(dataDirectory)
        .setCleanDataDirectory(false)
        .start()

fun startEphemeralEmbeddedPostgres(): EmbeddedPostgres = durableBuilder().start()

// zonky's defaults suit throwaway test databases: it starts Postgres with fsync and synchronous_commit off.
private fun durableBuilder(): EmbeddedPostgres.Builder =
    EmbeddedPostgres
        .builder()
        .setServerConfig("fsync", "on")
        .setServerConfig("synchronous_commit", "on")
        .setRegisterShutdownHook(false)
