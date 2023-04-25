package org.kontext.appdefine.dsl.impl

import org.kontext.appdefine.context.BeanManager
import org.kontext.appdefine.dsl.BeansDefine
import kotlin.reflect.KClass
import kotlin.reflect.cast

class BeanManagerSingletonDsl<T : Any>(
    private val kClass: KClass<T>,
    private val hostingDefine: BeansDefine,
    private val createMethod: (BeansDefine.() -> T),
    private val destroyMethod: (T.() -> Unit)? = null,
) : BeanManager<T> {

    var bean: T? = null

    override fun create() : T {
        bean = createMethod.invoke(hostingDefine)
        return bean!!
    }

    override fun hasDestroyMethod() = destroyMethod != null

    override fun destroy() {
        destroyMethod?.takeIf { kClass.isInstance(bean) }?.invoke(kClass.cast(bean))
    }

    override fun bean(): T? = bean
}