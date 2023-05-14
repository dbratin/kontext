package org.kontext.test.helpers

import java.nio.file.Files
import java.nio.file.Path

data class Options(
    val name: String,
    val fileRoot: Path,
)

fun <T : DirectedGraph.Node> visualize(options: Options, graph: DirectedGraph<T>, presentor: (T) -> String) {
    createHtml(options, createJson(options, graph, presentor))
}

fun <T : DirectedGraph.Node> createJson(options: Options, graph: DirectedGraph<T>, presenter: (T) -> String): String {
    val nodes = graph.nodes.asSequence().map { n -> "{ \"bean\" : \"${presenter(n)}\" }" }.joinToString(",")
    val links = graph.nodes.asSequence()
        .flatMap { n ->
            graph.getConnected(n).asSequence()
                .map {
                    "{ \"source\": \"${presenter(n)}\", \"target\": \"${presenter(it)}\" }"
                }
        }.joinToString(",")
    val content = "{\"nodes\":[${nodes}],\"links\":[${links}]}".toByteArray()
    Files.write(options.fileRoot.resolve("${options.name}.json"), content)
    return "./${options.name}.json"
}

fun createHtml(options: Options, dataLink: String) {
    val content = """
        <html>
            <header>
                <script src="//unpkg.com/force-graph"></script>
            </header>
            <div id="graph"></div>
            <script>
            fetch('$dataLink').then(res => res.json()).then(data => {
              const Graph = ForceGraph()
              (document.getElementById('graph'))
                .graphData(data)
                .nodeId('bean')
                .nodeVal('1')
                .nodeLabel('bean')
                .linkSource('source')
                .linkTarget('target')
                .linkDirectionalArrowLength(6);
            });
          </script>
        </html>"
        """.trimIndent().toByteArray()
    Files.write(options.fileRoot.resolve("${options.name}.html"), content)
}
