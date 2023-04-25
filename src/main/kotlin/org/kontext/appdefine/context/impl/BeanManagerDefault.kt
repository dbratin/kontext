package org.kontext.appdefine.context.impl

import org.kontext.appdefine.context.BeanManager
import kotlin.reflect.KClass
import kotlin.reflect.cast

class BeanManagerDefault<T : Any>(
    private val kClass: KClass<T>,
    private val createMethod: () -> T,
    private val destroyMethod: (T.() -> Unit)? = null,
) : BeanManager<T> {

    private var bean: T? = null

    override fun create() : T {
        bean = createMethod.invoke()
        return bean!!
    }

    override fun hasDestroyMethod() = destroyMethod != null

    override fun destroy() {
        destroyMethod?.takeIf { kClass.isInstance(bean) }?.invoke(kClass.cast(bean))
    }

    override fun bean(): T? = bean
}