package dev.geovanne.pulse

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import kotlin.io.path.extension
import kotlin.io.path.readText
import kotlin.io.path.reader

class StackRulesTest {
    private val templates: List<Path> =
        Files.walk(Path.of("src/main/resources/templates")).use { paths ->
            paths.filter { it.extension == "html" }.toList()
        }

    private val messages: Properties =
        Properties().apply { Path.of("src/main/resources/i18n/messages.properties").reader().use { load(it) } }

    @Test
    fun `graphql-java is not on the classpath`() {
        assertThatThrownBy { Class.forName("graphql.GraphQL") }.isInstanceOf(ClassNotFoundException::class.java)
    }

    @Test
    fun `no template uses the Thymeleaf Layout Dialect`() {
        assertThat(templates).isNotEmpty
        templates.forEach { template ->
            assertThat(LAYOUT_DIALECT.containsMatchIn(template.readText())).describedAs(template.toString()).isFalse()
        }
    }

    @Test
    fun `no template loads a script or stylesheet from another origin`() {
        templates.forEach { template ->
            assertThat(EXTERNAL_SOURCE.containsMatchIn(template.readText())).describedAs(template.toString()).isFalse()
        }
    }

    @Test
    fun `template rules catch violations and allow fragment references`() {
        assertThat(LAYOUT_DIALECT.containsMatchIn("""<html layout:decorate="~{main}">""")).isTrue()
        assertThat(LAYOUT_DIALECT.containsMatchIn("""xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"""")).isTrue()
        assertThat(LAYOUT_DIALECT.containsMatchIn("""th:replace="~{sl/layout::assets}"""")).isFalse()
        assertThat(LAYOUT_DIALECT.containsMatchIn("""th:replace="~{layout/main :: layout(t, c)}"""")).isFalse()

        assertThat(EXTERNAL_SOURCE.containsMatchIn("""<script src="https://cdn.example.com/a.js">""")).isTrue()
        assertThat(EXTERNAL_SOURCE.containsMatchIn("""<script th:src="@{https://cdn.example.com/a.js}">""")).isTrue()
        assertThat(EXTERNAL_SOURCE.containsMatchIn("""<link href="//cdn.example.com/a.css">""")).isTrue()
        assertThat(EXTERNAL_SOURCE.containsMatchIn("""<script th:src="@{/webjars/htmx.org/dist/htmx.min.js}">""")).isFalse()
    }

    @Test
    fun `apostrophes are doubled only in messages that take arguments`() {
        assertThat(messages).isNotEmpty
        messages.forEach { (key, value) ->
            val message = value as String
            if (MESSAGE_ARGUMENT.containsMatchIn(message)) {
                assertThat(LONE_APOSTROPHE.containsMatchIn(message)).describedAs(key as String).isFalse()
            } else {
                assertThat(message).describedAs(key as String).doesNotContain("''")
            }
        }
    }

    companion object {
        private val LAYOUT_DIALECT = Regex("""\blayout:[a-z]|ultraq""")
        private val EXTERNAL_SOURCE = Regex("""\b(src|href)\s*=\s*["'](@\{)?(https?:)?//""")
        private val MESSAGE_ARGUMENT = Regex("""\{\d+""")
        private val LONE_APOSTROPHE = Regex("""(?<!')'(?!')""")
    }
}
