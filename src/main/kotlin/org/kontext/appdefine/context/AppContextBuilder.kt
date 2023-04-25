package org.kontext.appdefine.context

interface AppContextBuilder {
    fun <T : Any> resolveBean(beanDescriptor: BeanDescriptor<T>): T

    fun <T : Any> registerBean(beanDescriptor: BeanDescriptor<T>, beanManager: BeanManager<T>)
}