package org.kontext.appdefine.context.impl

import org.kontext.appdefine.context.AppContext
import org.kontext.appdefine.context.BeanDescriptor
import org.kontext.appdefine.context.BeanManager
import org.kontext.appdefine.exceptions.AppContextDestroyedException
import org.kontext.appdefine.exceptions.BeanDeclaredTwiceException
import org.kontext.appdefine.exceptions.BeanNotFoundException
import kotlin.reflect.KClass

class AppContextDefault : AppContext {

    private val beanRegister: MutableMap<BeanDescriptor<out Any>, BeanManager<out Any>> = HashMap()
    private var destroyed = false

    fun registerSingleton(descriptor: BeanDescriptor<*>, beanManager: BeanManager<*>) {
        ensureNotDestroyed()

        if(beanRegister.containsKey(descriptor))
            throw BeanDeclaredTwiceException(descriptor)

        beanRegister[descriptor] = beanManager
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> findBeanManager(descriptor: BeanDescriptor<T>) : BeanManager<T>? {
        return beanRegister[descriptor] as BeanManager<T>?
    }

    fun start(): AppContextDefault {
        for(e in beanRegister.entries) {
            e.value.create()
        }
        return this
    }

    override fun <T : Any> getBean(kClass: KClass<T>): T {
        return findBean(kClass) ?: throw BeanNotFoundException()
    }

    override fun <T : Any> getBean(beanDescriptor: BeanDescriptor<T>): T {
        return findBean(beanDescriptor) ?: throw BeanNotFoundException()
    }

    override fun <T : Any> findBean(kClass: KClass<T>): T? {
        ensureNotDestroyed()

        return findBean(BeanDescriptor(kClass))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> findBean(beanDescriptor: BeanDescriptor<T>): T? {
        ensureNotDestroyed()

        return beanRegister[beanDescriptor]?.bean() as T?
    }

    override fun destroy() {
        ensureNotDestroyed()

        for(beanDescriptor in HashSet(beanRegister.keys)) {
            val beanManager = beanRegister[beanDescriptor] ?: continue

            if(beanManager.hasDestroyMethod()) {
                swallowErrors { beanManager.destroy() }
            }

            beanRegister.remove(beanDescriptor)
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