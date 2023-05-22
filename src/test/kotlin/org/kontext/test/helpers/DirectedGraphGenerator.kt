package org.kontext.test.helpers

import kotlin.random.Random

class DirectedGraphGenerator {
    fun <T : DirectedGraph.Node> generateConnectedNoCyclesGraph(
        nodeCount: Int,
        connRange: IntRange,
        nodeSupplier: (Int) -> T,
    ): DirectedGraph<T> {
        return generateConnectedGraph(nodeCount, nodeSupplier, connRange).apply {
            removeCycles()
        }
    }

    fun <T : DirectedGraph.Node> generateConnectedGraph(
        nodeCount: Int,
        nodeSupplier: (Int) -> T,
        connRange: IntRange
    ): DirectedGraph<T> {
        val graph = DirectedGraph<T>()
        for (nodeNumber in 0 until nodeCount) {
            val newNode = nodeSupplier(nodeNumber)
            graph.add(newNode)
        }

        val nodesList = ArrayList(graph.nodes)

        for (nodeFrom in graph.nodes) {
            val connCount = generateConnCount(connRange)
            val avoidNodes: Set<T> = HashSet<T>().apply {
                add(nodeFrom)
                addAll(graph.findReferring(nodeFrom))
            }
            for (nodeTo in pickNodesToConnect(connCount, nodesList, avoidNodes)) {
                graph.connect(nodeFrom, nodeTo)
            }
        }

        return graph
    }

    private fun <T : DirectedGraph.Node> pickNodesToConnect(
        connCount: Int,
        allNodes: ArrayList<T>,
        avoidNodes: Set<T>
    ): Set<T> {
        val nodesToConnect = HashSet<T>()
        for (i in 0 until connCount) {
            var num = 0
            while (num < allNodes.size * 2) {
                val index = Random.nextInt(0, allNodes.size - 1)
                val node = allNodes[index]
                if (avoidNodes.contains(node) || nodesToConnect.contains(node)) {
                    num++
                    continue
                } else {
                    nodesToConnect.add(node)
                    break
                }
            }
            if (num >= allNodes.size * 2) {
                throw IllegalStateException("Too much attempts to pick node to connect")
            }
        }
        return nodesToConnect
    }

    private fun generateConnCount(conRange: IntRange) = Random.nextInt(conRange.first, conRange.last)

    private fun <T : DirectedGraph.Node> DirectedGraph<T>.removeCycles() {
        for (node in nodes) {
            val referringNodes = findReferring(node)
            val visited = HashSet<T>().apply { add(node) }
            val referred = removeCycles(node, referringNodes, visited)

            visited.addAll(referred)
        }
    }

    private fun <T : DirectedGraph.Node> DirectedGraph<T>.removeCycles(
        node: T,
        referringNodes: Set<T>,
        visited: Set<T>,
    ): Set<T> {
        val referred = getConnected(node).asSequence().filter { !visited.contains(it) }.toSet()
        val toDelete = referringNodes.asSequence()
            .filter { referred.contains(it) }
            .toList()
        for (delete in toDelete) {
            removeEdge(delete, node)
        }
        return referred
    }
}