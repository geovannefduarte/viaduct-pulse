package dev.geovanne.pulse.system.web

import dev.geovanne.pulse.system.application.DatabaseStatusService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/status")
class StatusController(
    private val databaseStatusService: DatabaseStatusService,
) {
    @GetMapping
    @HxRequest(target = "status-card")
    fun statusCard(model: Model): String {
        model.addAttribute("status", databaseStatusService.current())
        return "system/status :: status-card(status=\${status})"
    }

    @GetMapping
    fun statusPage(model: Model): String {
        model.addAttribute("status", databaseStatusService.current())
        return "home/home"
    }
}
