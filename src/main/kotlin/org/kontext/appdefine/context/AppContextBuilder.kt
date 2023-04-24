package org.kontext.appdefine.context

import org.kontext.appdefine.dsl.AppContextDelegate
import org.kontext.appdefine.exceptions.BeanDeclaredTwiceException

class AppContextBuilder : AppContextDelegate {
    private val beansDefinitions: MutableMap<BeanDescriptor<out Any>, BeanManager<out Any>> = LinkedHashMap()
    private val context = AppContext()

    fun <T : Any> addBeanDefinition(descriptor: BeanDescriptor<T>, manager: BeanManager<T>) {
        if(beansDefinitions.containsKey(descriptor))
            throw BeanDeclaredTwiceException(descriptor)

        beansDefinitions[descriptor] = manager
    }

    fun addAllBeanDefinitions(beanDefinitions: Map<BeanDescriptor<*>, BeanManager<*>>) {
        beanDefinitions.keys.find { beanDefinitions.keys.contains(it) }?.run {
            throw BeanDeclaredTwiceException(this)
        }

        beansDefinitions.putAll(beanDefinitions)
    }

    fun build(): AppContext {
        for(e in beansDefinitions.entries) {
            // ensure that bean created
            e.value.create()
            // register it
            // TODO: route beans by scopes
            context.addSingleton(e.key, e.value)
        }
        return context
    }

    override fun <T : Any> resolveBean(beanDescriptor: BeanDescriptor<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> registerBean(beanDescriptor: BeanDescriptor<T>, beanManager: BeanManager<T>) {
        addBeanDefinition(beanDescriptor, beanManager)
    }
}