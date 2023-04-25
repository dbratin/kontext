package org.kontext.appdefine.context

import kotlin.reflect.KClass

interface AppContext {
    fun <T : Any> getBean(kClass: KClass<T>): T
    fun <T : Any> getBean(beanDescriptor: BeanDescriptor<T>): T

    fun <T : Any> findBean(kClass: KClass<T>): T?

    fun <T : Any> findBean(beanDescriptor: BeanDescriptor<T>): T?

    fun destroy()
}