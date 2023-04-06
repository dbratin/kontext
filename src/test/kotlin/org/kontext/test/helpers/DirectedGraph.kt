package org.kontext.test.helpers

class DirectedGraph<T : DirectedGraph.Node> {
    interface Node

    private val adjacentMatrix: MutableMap<T, MutableSet<T>> = HashMap()

    val nodes: Set<T>
        get() = adjacentMatrix.keys

    fun add(node: T) {
        adjacentMatrix[node] = HashSet()
    }

    fun connect(nodeFrom: T, nodeTo: T) {
        if(!adjacentMatrix.contains(nodeFrom))
            throw NodeNotFoundException(nodeFrom)

        if(!adjacentMatrix.contains(nodeTo))
            throw NodeNotFoundException(nodeTo)

        adjacentMatrix[nodeFrom]?.add(nodeTo)
    }

    fun getConnected(node: T): Set<T> = adjacentMatrix[node] ?: throw NodeNotFoundException(node)
}

class NodeNotFoundException(val node: DirectedGraph.Node) : Exception()
