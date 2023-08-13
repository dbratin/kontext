package org.kontext.test.helpers

import java.util.*
import kotlin.random.Random

class DirectedGraphGenerator {
    fun <T : DirectedGraph.Node> generateConnectedNoCyclesGraph(
        nodeCount: Int,
        connRange: IntRange,
        nodeSupplier: (Int) -> T,
    ): DirectedGraph<T> {
        val graph = createGraphWithNodes(nodeCount, nodeSupplier)

        val shuffledNodes = LinkedList(graph.nodes).apply { shuffle() }
        val sourceNodes = ArrayList<NodeAndCounter<T>>()

        val greatAncestorsCount = (nodeCount / connRange.last).takeIf { it > 0 } ?: 1
        for (i in 1..greatAncestorsCount) {
            sourceNodes.add(NodeAndCounter(shuffledNodes.pollFirst(), generateConnCount(connRange)))
        }

        while(shuffledNodes.isNotEmpty()) {
            val target = shuffledNodes.pollFirst()
            for(i in 1..generateConnCount(connRange)) {
                var sourceNodeCounter = pickRandom(sourceNodes)!!
                var attempts = 0
                while(!sourceNodeCounter.havePlace() && attempts < sourceNodes.size) {
                    sourceNodeCounter = pickRandom(sourceNodes)!!
                    attempts++
                }

                graph.connect(sourceNodeCounter.node, target)

                sourceNodeCounter.occupy()
                sourceNodes.add(NodeAndCounter(target, generateConnCount(connRange)))
            }
        }

        return graph
    }

    private fun <T : DirectedGraph.Node> createGraphWithNodes(
        nodeCount: Int,
        nodeSupplier: (Int) -> T
    ): DirectedGraph<T> {
        val graph = DirectedGraph<T>()
        for (nodeNumber in 0 until nodeCount) {
            val newNode = nodeSupplier(nodeNumber)
            graph.add(newNode)
        }
        return graph
    }

    private fun generateConnCount(conRange: IntRange) = Random.nextInt(conRange.first, conRange.last).takeIf { it > 0 } ?: 1

    private fun <T> pickRandom(collection: List<T>): T? =
        collection.takeIf { it.isNotEmpty() }?.get(Random.nextInt(collection.size))

    private data class NodeAndCounter<T>(val node: T, val places: Int) {
        var occupied = 0
        fun havePlace() = occupied < places

        fun occupy() { occupied++ }
    }
}