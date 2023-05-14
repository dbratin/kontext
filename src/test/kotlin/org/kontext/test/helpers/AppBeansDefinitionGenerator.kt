package org.kontext.test.helpers

import com.squareup.kotlinpoet.*
import org.kontext.appdefine.context.AppContextBuilder
import org.kontext.appdefine.context.impl.AppContextDefault
import org.kontext.appdefine.dsl.AppDefine
import java.nio.file.Path

class AppBeansDefinitionGenerator(private val sourceRoot: Path) {

    val definitionSize = 100

    fun generateDefinition(definitionName: String, outputPackage: String, graph: DirectedGraph<BeanNode>) {
        generateBeanClasses(outputPackage, graph)

        val subdefs = graph.nodes.asSequence().chunked(definitionSize)
            .mapIndexed { index, chunk ->
                Pair(subDefName(definitionName, index), chunk)
            }
            .associateBy({ p -> p.first }, { p-> p.second })

        subdefs.forEach {
            generateBeansDefinition(outputPackage, it.key, it.value, graph)
        }

        generateRootAppDefinition(outputPackage, definitionName, subdefs.keys)
    }

    private fun generateRootAppDefinition(outputPackage: String, definitionName: String, subdefsNames: Collection<String>) {
        val definitionSrcFile = FileSpec.builder(outputPackage, definitionName)
        val className = ClassName(outputPackage, definitionName)

        val importsBuilder = CodeBlock.builder()
            .add("{\n")
            .add("import(\n".tab(1))

        for(subdef in subdefsNames) {
            importsBuilder.add("$subdef::class,\n".tab(2))
        }

        importsBuilder
            .add(")\n".tab(1))
            .add("}")

        definitionSrcFile
            .addType(
                TypeSpec.classBuilder(className)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("contextBuilder", AppContextBuilder::class)
                            .build()
                    )
                    .superclass(AppDefine::class)
                    .addSuperclassConstructorParameter("contextBuilder")
                    .addSuperclassConstructorParameter(importsBuilder.build())
                    .build()
            )

        definitionSrcFile.build().writeTo(sourceRoot)
    }

    private fun generateBeansDefinition(
        outputPackage: String,
        definitionName: String,
        nodes: List<BeanNode>,
        graph: DirectedGraph<BeanNode>,
    ) {
        val definitionSrcFile = FileSpec.builder(outputPackage, definitionName)
        val className = ClassName(outputPackage, definitionName)

        val beanDefinitionBuilder = CodeBlock.builder()
            .add("{\n")
            .add("beans {\n".tab(1))
        for (bean in nodes) {
            beanDefinitionBuilder
                .add("singleton {\n".tab(2))
                .add("${bean.name}(\n".tab(3))
            for (dependency in graph.getConnected(bean)) {
                beanDefinitionBuilder.add("${dependency.name.toParamName()} = bean(),\n".tab(4))
            }
            beanDefinitionBuilder
                .add(")\n".tab(3))
                .add("}\n".tab(2))
        }
        val appDefinition = beanDefinitionBuilder
            .add("}\n".tab(1))
            .add("}")
            .build()

        definitionSrcFile
            .addType(
                TypeSpec.classBuilder(className)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("contextBuilder", AppContextBuilder::class)
                            .build()
                    )
                    .superclass(AppDefine::class)
                    .addSuperclassConstructorParameter("contextBuilder")
                    .addSuperclassConstructorParameter(appDefinition)
                    .build()
            )

        definitionSrcFile.build().writeTo(sourceRoot)
    }

    private fun generateBeanClasses(outputPackage: String, graph: DirectedGraph<BeanNode>) {
        for (node in graph.nodes) {
            val constructorBuilder = FunSpec.constructorBuilder()

            for (arg in graph.getConnected(node)) {
                constructorBuilder.addParameter(arg.name.toParamName(), ClassName(outputPackage, arg.name))
            }

            val classSpec = TypeSpec.classBuilder(ClassName(outputPackage, node.name))
                .primaryConstructor(constructorBuilder.build())
                .build()

            val fileSpec = FileSpec.builder(outputPackage, node.name)
                .addType(classSpec)
                .build()

            fileSpec.writeTo(sourceRoot)
        }
    }

    private fun String.tab(i: Int): String {
        return " ".repeat(4 * i) + this
    }

    private fun String.toParamName(): String {
        return "${substring(0, 1).lowercase()}${substring(1)}"
    }

    private fun subDefName(mainName: String, index: Int) = "$mainName$index"
}

data class BeanNode(val name: String) : DirectedGraph.Node