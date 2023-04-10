package org.kontext.test.helpers

import com.squareup.kotlinpoet.*
import org.kontext.appdefine.context.AppContext
import org.kontext.appdefine.dsl.AppDefine
import java.nio.file.Path

class AppBeansDefinitionGenerator(private val sourceRoot: Path) {
    fun generateDefinition(definitionName: String, outputPackage: String, graph: DirectedGraph<BeanNode>) {
        generateBeanClasses(outputPackage, graph)

        val definitionSrcFile = FileSpec.builder(outputPackage, definitionName)
        val className = ClassName(outputPackage, definitionName)

        val beanDefinitionBuilder = CodeBlock.builder()
                .add("{\n")
                .add("beans {\n".tab(1))
        for(bean in graph.nodes) {
            beanDefinitionBuilder
                    .add("singleton {\n".tab(2))
                    .add("${bean.name}(\n".tab(3))
            for(dependency in graph.getConnected(bean)) {
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
                                                .addParameter("context", AppContext::class)
                                                .build()
                                )
                                .superclass(AppDefine::class)
                                .addSuperclassConstructorParameter("context")
                                .addSuperclassConstructorParameter(appDefinition)
                                .build()
                )

        definitionSrcFile.build().writeTo(sourceRoot)
    }

    private fun generateBeanClasses(outputPackage: String, graph: DirectedGraph<BeanNode>) {
        for(node in graph.nodes) {
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
}

data class BeanNode(val name: String) : DirectedGraph.Node