package dev.geovanne.pulse.database

import com.zaxxer.hikari.HikariDataSource
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class EmbeddedPostgresConfiguration {
    @Bean(destroyMethod = "close")
    fun embeddedPostgres(properties: PostgresProperties): EmbeddedPostgres = startEmbeddedPostgres(properties.dataDirectory)

    @Bean(destroyMethod = "close")
    fun dataSource(postgres: EmbeddedPostgres): HikariDataSource =
        HikariDataSource().apply {
            jdbcUrl = postgres.getJdbcUrl("postgres", "postgres")
            username = "postgres"
        }
}
