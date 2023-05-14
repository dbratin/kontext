package org.kontext.test.helpers

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TreeGeneratorTest {
    @Test
    fun generateRandomTree() {
        val generator = TreeGenerator()
        val tree = generator.generateRandomTree(80, 1..10) { nodeNumber -> NumberedNode(nodeNumber) }

        tree.root.number shouldBe 0
        tree.graph.nodes shouldHaveSize 80

        assertHaveNoCycles(tree)
        //visualize(tree.graph)
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
}