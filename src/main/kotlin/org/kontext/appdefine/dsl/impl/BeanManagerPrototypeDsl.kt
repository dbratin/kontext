package org.kontext.appdefine.dsl.impl

import org.kontext.appdefine.context.BeanManager
import org.kontext.appdefine.dsl.BeansDefine
import kotlin.reflect.KClass
import kotlin.reflect.cast

class BeanManagerPrototypeDsl<T : Any>(
    private val hostingDefine: BeansDefine,
    private val createMethod: (BeansDefine.() -> T),
) : BeanManager<T> {

    override fun create() : T = createMethod.invoke(hostingDefine)

    override fun hasDestroyMethod() = false

    override fun destroy() {
        throw NotImplementedError()
    }

    override fun bean(): T = create()
}