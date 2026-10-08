package dev.geovanne.pulse

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.readText

class StackRulesTest {
    private val templates: List<Path> =
        Files.walk(Path.of("src/main/resources/templates")).use { paths ->
            paths.filter { it.extension == "html" }.toList()
        }

    @Test
    fun `graphql-java is not on the classpath`() {
        assertThatThrownBy { Class.forName("graphql.GraphQL") }.isInstanceOf(ClassNotFoundException::class.java)
    }

    @Test
    fun `no template uses the Thymeleaf Layout Dialect`() {
        assertThat(templates).isNotEmpty
        templates.forEach { template ->
            assertThat(template.readText()).describedAs(template.toString()).doesNotContain("layout:", "ultraq")
        }
    }

    @Test
    fun `no template loads a script or stylesheet from another origin`() {
        val externalSource = Regex("""(src|href)\s*=\s*["'](https?:)?//""")
        templates.forEach { template ->
            assertThat(externalSource.containsMatchIn(template.readText())).describedAs(template.toString()).isFalse()
        }
    }
}
