package org.kontext.test.helpers

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Path

class AppBeansDefinitionGeneratorTest {

    private val generator = AppBeansDefinitionGenerator(Path.of("src/test/kotlin"))

    @Test
    @Disabled("for manual validation")
    fun tryIt() {
        generator.generateDefinition(
            "GeneratedAppDefinition",
            "org.kontext.appdefine.generated.small",
            TreeGenerator().generateRandomTree(8, 1..2) { i ->
                BeanNode("Bean$i")
            }.graph
        )
    }

    @Test
    //@Disabled("for manual code generation")
    fun generateHuge8000() {
        generator.generateDefinition(
            "HugeAppDefinition8000",
            "org.kontext.appdefine.generated.huge8000",
            TreeGenerator().generateRandomTree(1000, 1..20) { i ->
                BeanNode("HugeContext800Bean$i")
            }.graph
        )
    }
}