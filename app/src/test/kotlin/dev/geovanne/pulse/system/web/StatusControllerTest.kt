package dev.geovanne.pulse.system.web

import dev.geovanne.pulse.system.application.DatabaseStatusService
import dev.geovanne.pulse.system.domain.DatabaseStatuses.aDatabaseStatus
import org.assertj.core.api.Assertions.assertThat
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StatusController::class)
class StatusControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webClient: WebClient

    @MockitoBean
    lateinit var databaseStatusService: DatabaseStatusService

    @BeforeEach
    fun setUp() {
        given(databaseStatusService.current()).willReturn(aDatabaseStatus())
        webClient.options.isJavaScriptEnabled = false
    }

    @Test
    fun `htmx request targeting the card returns only the card`() {
        val body = bodyOf(get("/status").header("HX-Request", "true").header("HX-Target", "status-card"))

        assertThat(body).doesNotContain("<html", "<head")
        assertThat(body.trim()).startsWith("<div id=\"status-card\"")
        assertThat(body).contains("id=\"postgres-version\">18.6<")
        assertThat(body).doesNotContain("hx-get")
    }

    @Test
    fun `plain request returns the full page with the card filled`() {
        val page: HtmlPage = webClient.getPage("/status")

        assertThat(page.titleText).isEqualTo("Home · Pulse")
        assertThat(page.getElementById("postgres-version").textContent).isEqualTo("18.6")
        assertThat(page.getElementById("applied-migrations").textContent.trim()).isEqualTo("V1 create sync state")
    }

    @Test
    fun `htmx request with another target returns the full page`() {
        val body = bodyOf(get("/status").header("HX-Request", "true").header("HX-Target", "main"))

        assertThat(body).contains("<html")
    }

    @Test
    fun `history restore request returns the full page`() {
        val body =
            bodyOf(
                get("/status")
                    .header("HX-Request", "true")
                    .header("HX-Target", "status-card")
                    .header("HX-History-Restore-Request", "true"),
            )

        assertThat(body).contains("<html")
    }

    private fun bodyOf(request: MockHttpServletRequestBuilder): String =
        mockMvc
            .perform(request)
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString
}
