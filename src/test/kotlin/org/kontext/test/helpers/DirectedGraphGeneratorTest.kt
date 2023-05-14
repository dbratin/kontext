package org.kontext.test.helpers

import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import java.nio.file.Path

class DirectedGraphGeneratorTest {
    @Test
    fun generateConnectedNoCyclesGraph() {
        val generator = DirectedGraphGenerator()
        val graph = generator.generateConnectedNoCyclesGraph(50, 1..7) { i ->
            NumberedNode(i)
        }

        graph.nodes shouldHaveSize 50
        val options = Options(
            name = "directed-graph",
            fileRoot = Path.of("src/test/resources/directed-graph")
        )
        visualize(options, graph) { n -> "${n.number}" }
    }
}