package org.kontext.appdefine.dsl

import org.kontext.appdefine.exceptions.BeanDefinitionNotFoundException
import org.kontext.appdefine.context.AppContext
import org.kontext.appdefine.context.BeanDescriptor
import kotlin.reflect.KClass

class BeansDefine(val context: AppContext, beansDefinition: BeansDefine.() -> Unit) {

    private val definitions: MutableMap<BeanDescriptor<*>, BeansDefine.() -> Any> = LinkedHashMap()

    init {
        apply { beansDefinition() }
        createDefined()
    }

    inline fun <reified T : Any> singleton(noinline beanDefinition: BeansDefine.() -> T) {
        registerBeanDefinition(BeanDescriptor(T::class), beanDefinition)
    }

    inline fun <reified T : Any> bean(): T {
        return context.findBean(T::class) ?: createBean(BeanDescriptor(T::class)).also { newBean ->
            context.addSingleton(newBean)
        }
    }

    fun registerBeanDefinition(descriptor: BeanDescriptor<*>, beanDefinition: BeansDefine.() -> Any) {
        definitions[descriptor] = beanDefinition
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> createBean(descriptor: BeanDescriptor<T>): T {
        val bean = definitions[descriptor]?.let { beanDefinition ->
            this.beanDefinition()
        } ?: throw BeanDefinitionNotFoundException()

        return bean as T
    }

    private fun createDefined() {
        for(e in definitions.entries) {
            context.addSingleton(e.value.invoke(this))
        }
    }


}