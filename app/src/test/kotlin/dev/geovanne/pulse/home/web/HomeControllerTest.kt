package dev.geovanne.pulse.home.web

import org.assertj.core.api.Assertions.assertThat
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlElement
import org.htmlunit.html.HtmlPage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest

@WebMvcTest(HomeController::class)
class HomeControllerTest {
    @Autowired
    lateinit var webClient: WebClient

    @BeforeEach
    fun disableJavaScript() {
        webClient.options.isJavaScriptEnabled = false
    }

    @Test
    fun `home page shows the status placeholder that htmx loads`() {
        val page: HtmlPage = webClient.getPage("/")

        val placeholder = page.getHtmlElementById<HtmlElement>("status-card")
        val loader = placeholder.getFirstByXPath<HtmlElement>("./div[@hx-get]")
        assertThat(loader.getAttribute("hx-get")).isEqualTo("/status")
        assertThat(page.getElementById("postgres-version")).isNull()
        assertThat(loader.getAttribute("hx-trigger")).isEqualTo("load, retry")
        assertThat(placeholder.querySelector<HtmlElement>("[data-request-error-slot]")).isNotNull
    }

    @Test
    fun `layout carries the request error template and handler`() {
        val page: HtmlPage = webClient.getPage("/")

        val template = page.getElementById("request-error").asXml()
        assertThat(template).contains("This part of the page couldn't load.", "data-request-retry", "Try again")
        assertThat(page.getElementById("request-errors").getAttribute("data-message"))
            .isEqualTo("Part of this page couldn't load. Reload the page to try again.")
        assertThat(page.getByXPath<HtmlElement>("//script[@src='/js/request-errors.js']")).hasSize(1)
    }

    @Test
    fun `layout renders the Shadleaf header with a theme toggle`() {
        val page: HtmlPage = webClient.getPage("/")

        assertThat(page.titleText).isEqualTo("Home · Pulse")
        assertThat(page.getElementById("site-name").textContent.trim()).isEqualTo("Pulse")
        assertThat(page.getElementById("theme-toggle-trigger")).isNotNull()
        assertThat(page.getElementById("main")).isNotNull()
    }
}
