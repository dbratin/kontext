package org.kontext.appdefine.dsl

import org.kontext.appdefine.context.BeanDescriptor
import org.kontext.appdefine.context.BeanManager

interface AppContextDelegate {
    fun <T : Any> resolveBean(beanDescriptor: BeanDescriptor<T>): T

    fun <T : Any> registerBean(beanDescriptor: BeanDescriptor<T>, beanManager: BeanManager<T>)
}