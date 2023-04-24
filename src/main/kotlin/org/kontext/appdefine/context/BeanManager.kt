package org.kontext.appdefine.context

import org.kontext.appdefine.dsl.BeansDefine
import org.kontext.appdefine.exceptions.BeanNotInitializedException
import kotlin.reflect.KClass
import kotlin.reflect.cast

class BeanManager<T : Any>(
    private val kClass: KClass<T>,
    private val hostingDefine: BeansDefine,
    private val createMethod: (BeansDefine.() -> T),
    private val destroyMethod: (T.() -> Unit)? = null,
) {

    private var bean: T? = null

    fun create() : T {
        bean = createMethod.invoke(hostingDefine)
        return bean!!
    }

    fun hasDestroyMethod() = destroyMethod != null

    fun destroy() {
        destroyMethod?.takeIf { kClass.isInstance(bean) }?.invoke(kClass.cast(bean))
    }

    fun getBean(): T? = bean
}