package org.kontext.appdefine.context.impl

import org.kontext.appdefine.context.BeanDescriptor
import org.kontext.appdefine.context.BeanManager
import org.kontext.appdefine.context.AppContextBuilder
import org.kontext.appdefine.exceptions.BeanDefinitionNotFoundException

class AppContextDefaultBuilder : AppContextBuilder {

    private val context = AppContextDefault()

    fun build(): AppContextDefault {
        return context.start()
    }

    override fun <T : Any> resolveBean(beanDescriptor: BeanDescriptor<T>): T {
        val manager = context.findBeanManager(beanDescriptor) ?: throw BeanDefinitionNotFoundException(beanDescriptor)
        return manager.let { it.bean() ?: it.create() }
    }

    override fun <T : Any> registerBean(beanDescriptor: BeanDescriptor<T>, beanManager: BeanManager<T>) {
        context.registerSingleton(beanDescriptor, beanManager)
    }
}