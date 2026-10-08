package dev.geovanne.pulse.database

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path

@ConfigurationProperties("pulse.postgres")
data class PostgresProperties(
    val dataDirectory: Path = Path.of(".pulse", "pgdata"),
)
