package dev.geovanne.pulse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class PulseApplication

fun main(args: Array<String>) {
    runApplication<PulseApplication>(*args)
}
