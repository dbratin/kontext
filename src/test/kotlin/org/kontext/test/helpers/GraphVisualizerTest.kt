package org.kontext.test.helpers

import org.junit.jupiter.api.Test
import java.nio.file.Path

class GraphVisualizerTest {
    @Test
    fun runIt() {
        val graph = TreeGenerator().generateRandomTree(80, 0..4) {
            i -> NumberedNode(i)
        }
        visualize(Options(name = "testGraph", fileRoot = Path.of("src/test/resources")), graph.graph) {
            n -> "${n.number}"
        }
    }

    data class NumberedNode(val number: Int) : DirectedGraph.Node
}