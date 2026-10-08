package dev.geovanne.pulse.system.web

import dev.geovanne.pulse.system.application.DatabaseStatusService
import dev.geovanne.pulse.system.domain.DatabaseStatus
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/status")
class StatusController(
    private val databaseStatusService: DatabaseStatusService,
) {
    @ModelAttribute("status")
    fun status(): DatabaseStatus = databaseStatusService.current()

    @GetMapping
    @HxRequest(target = "status-card")
    fun statusCard(): String = "system/status :: status-card(status=\${status})"

    @GetMapping
    fun statusPage(): String = "home/home"
}
