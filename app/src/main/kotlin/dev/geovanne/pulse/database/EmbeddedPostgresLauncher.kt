package dev.geovanne.pulse.database

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import java.nio.file.Path

fun startEmbeddedPostgres(dataDirectory: Path): EmbeddedPostgres =
    EmbeddedPostgres
        .builder()
        .setDataDirectory(dataDirectory)
        .setCleanDataDirectory(false)
        .setRegisterShutdownHook(false)
        .start()
