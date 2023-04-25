package org.kontext.appdefine.context

interface BeanManager<T : Any> {
    fun create(): T
    fun hasDestroyMethod(): Boolean
    fun destroy()
    fun bean(): T?
}