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

    fun removeEdge(nodeFrom: T, nodeTo: T) {
        if(!adjacentMatrix.contains(nodeFrom))
            throw NodeNotFoundException(nodeFrom)

        if(!adjacentMatrix.contains(nodeTo))
            throw NodeNotFoundException(nodeTo)

        adjacentMatrix[nodeFrom]?.remove(nodeTo)
    }

    fun getConnected(node: T): Set<T> = adjacentMatrix[node] ?: throw NodeNotFoundException(node)

    fun findReferring(node: T): Set<T> = adjacentMatrix.asSequence()
        .filter { e -> e.value.contains(node) }
        .map { e -> e.key }
        .toSet()

    fun findPath(nodeFrom: T, nodeTo: T): List<Node> {
        return emptyList()
    }
}

class NodeNotFoundException(val node: DirectedGraph.Node) : Exception()
