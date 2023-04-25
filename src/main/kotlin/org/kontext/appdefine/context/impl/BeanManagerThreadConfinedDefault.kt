package org.kontext.appdefine.context.impl

import org.kontext.appdefine.context.BeanManager
import org.kontext.appdefine.dsl.BeansDefine
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KClass
import kotlin.reflect.cast

class BeanManagerThreadConfinedDefault<T : Any> (
    private val kClass: KClass<T>,
    private val createMethod: () -> T,
    private val destroyMethod: (T.() -> Unit)? = null,
) : BeanManager<T> {

    private val bean: ThreadLocal<T?> = ThreadLocal()
    private val allBeans: ConcurrentLinkedQueue<T> = ConcurrentLinkedQueue()
    override fun create(): T {
        val newInstance = createMethod.invoke()
        allBeans.add(newInstance)
        bean.set(newInstance)
        return bean.get()!!
    }

    override fun hasDestroyMethod() = destroyMethod != null

    override fun destroy() {
        if(destroyMethod != null) {
            for (localBean in allBeans) {
                destroyMethod.takeIf { kClass.isInstance(localBean) }?.invoke(kClass.cast(localBean))
            }
        }
    }

    override fun bean(): T = bean.get() ?: create()
}