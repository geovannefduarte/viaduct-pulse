package dev.geovanne.pulse

import dev.geovanne.pulse.system.application.DatabaseStatusService
import org.assertj.core.api.Assertions.assertThat
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["pulse.postgres.ephemeral=true"])
class ErrorPagesTest {
    @LocalServerPort
    var port: Int = 0

    @MockitoBean
    lateinit var databaseStatusService: DatabaseStatusService

    private val webClient =
        WebClient().apply {
            options.isJavaScriptEnabled = false
            options.isThrowExceptionOnFailingStatusCode = false
        }

    @AfterEach
    fun closeBrowser() {
        webClient.close()
    }

    @Test
    fun `unknown path renders the 404 page`() {
        val page: HtmlPage = webClient.getPage("http://localhost:$port/no-such-page")

        assertThat(page.webResponse.statusCode).isEqualTo(404)
        assertThat(page.titleText).isEqualTo("Page not found · Pulse")
        assertThat(page.getElementById("page-content").textContent).contains("The page you asked for doesn't exist.")
    }

    @Test
    fun `server error renders the 5xx page`() {
        given(databaseStatusService.current()).willThrow(IllegalStateException("database unavailable"))

        val page: HtmlPage = webClient.getPage("http://localhost:$port/status")

        assertThat(page.webResponse.statusCode).isEqualTo(500)
        assertThat(page.titleText).isEqualTo("Something went wrong · Pulse")
        assertThat(page.getElementById("page-content").textContent)
            .contains("The server couldn't complete your request.")
    }
}
