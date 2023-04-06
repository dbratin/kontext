package org.kontext.test.helpers

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GraphGeneratorTest {
    @Test
    fun generateRandomTree() {
        val generator = GraphGenerator()
        val tree = generator.generateRandomTree(80, 1..10) { nodeNumber -> NumberedNode(nodeNumber) }

        tree.root.number shouldBe 0
        tree.graph.nodes shouldHaveSize 80

        assertHaveNoCycles(tree)
    }

    private fun assertHaveNoCycles(tree: Tree<NumberedNode>) {
        val visited = HashSet<NumberedNode>()
        val toVisit = ArrayList<NumberedNode>(listOf(tree.root))
        while (toVisit.isNotEmpty()) {
            val node = toVisit.removeFirst()

            visited.contains(node) shouldBe false

            toVisit.addAll(tree.graph.getConnected(node))
            visited.add(node)
        }

        visited shouldHaveSize tree.graph.nodes.size
    }

    data class NumberedNode(val number: Int) : DirectedGraph.Node
}