package org.kontext.appdefine.context.impl

import org.kontext.appdefine.context.BeanManager
import org.kontext.appdefine.dsl.BeansDefine

class BeanManagerPrototypeDefault<T : Any>(
    private val createMethod: () -> T,
) : BeanManager<T> {

    override fun create() : T = createMethod.invoke()

    override fun hasDestroyMethod() = false

    override fun destroy() {
        throw NotImplementedError()
    }

    override fun bean(): T = create()
}