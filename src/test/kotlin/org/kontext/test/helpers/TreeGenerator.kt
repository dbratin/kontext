package org.kontext.test.helpers

import kotlin.random.Random

class TreeGenerator {
    fun <T: DirectedGraph.Node> generateRandomTree(nodeCount: Int, childrenCountRange: IntRange, nodeSupplier: (Int) -> T): Tree<T> {
        val graph = DirectedGraph<T>()
        val nodesIndex = ArrayList<T>(nodeCount)
        for(nodeNumber in 0 until nodeCount) {
            val newNode = nodeSupplier(nodeNumber)
            nodesIndex.add(nodeNumber, newNode)
            graph.add(newNode)
        }

        val root = nodesIndex.removeFirst()
        val parents = ArrayList<T>(listOf(root))
        while(nodesIndex.isNotEmpty()) {
            parents.addAll(assembleParentAndChild(parents.removeFirst(), nodesIndex, graph, childrenCountRange))
        }
        return Tree(root, graph)
    }

    private fun <T: DirectedGraph.Node> assembleParentAndChild(
        parent: T,
        nodesIndex: ArrayList<T>,
        graph: DirectedGraph<T>,
        childrenCountRange: IntRange,
    ): List<T> {
        val childrenCount = Random.nextInt(childrenCountRange.first, childrenCountRange.last)
        val children = ArrayList<T>()
        for(nodeNumber in 0..childrenCount) {
            if(nodesIndex.isEmpty()) break

            val child = nodesIndex.removeFirst()
            graph.connect(parent, child)
            children.add(child)
        }
        return children
    }
}

data class Tree<T : DirectedGraph.Node>(val root: T, val graph: DirectedGraph<T>)