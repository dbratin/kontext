package org.kontext.appdefine.context

import org.kontext.appdefine.exceptions.AppContextDestroyedException
import org.kontext.appdefine.exceptions.BeanNotFoundException
import kotlin.reflect.KClass

class AppContext {

    private val singletons: MutableMap<BeanDescriptor<out Any>, Any> = HashMap()
    private var destroyed = false

    fun <T : Any> addSingleton(bean: T) {
        ensureNotDestroyed()

        singletons[BeanDescriptor(bean::class)] = bean
    }

    fun <T : Any> addSingleton(descriptor: BeanDescriptor<T>, bean: T) {
        ensureNotDestroyed()

        singletons[descriptor] = bean
    }

    fun <T : Any> getBean(kClass: KClass<T>): T {
        return findBean(kClass) ?: throw BeanNotFoundException()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> findBean(kClass: KClass<T>): T? {
        ensureNotDestroyed()

        return singletons[BeanDescriptor(kClass)] as T?
    }

    fun destroy() {
        ensureNotDestroyed()

        for(beanDescriptor in HashSet(singletons.keys)) {
            val bean = singletons[beanDescriptor] ?: continue

            if(beanDescriptor.hasDestroyMethod()) {
                swallowErrors { beanDescriptor.applyDestroyMethod(bean) }
            } else if(bean is AutoCloseable) {
                swallowErrors { bean.close() }
            }

            singletons.remove(beanDescriptor)
        }
        destroyed = true
    }

    private fun ensureNotDestroyed() {
        if(destroyed)
            throw AppContextDestroyedException()
    }

    private fun swallowErrors(statement: () -> Unit) {
        try {
            statement()
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun logException(exception: Exception) {
        //FIXME: give an adequate implementation
        println("ERROR: ${exception.message}")
        exception.printStackTrace()
    }


}