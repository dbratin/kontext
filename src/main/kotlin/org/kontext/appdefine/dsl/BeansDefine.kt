package org.kontext.appdefine.dsl

import org.kontext.appdefine.context.BeanDescriptor
import org.kontext.appdefine.context.BeanManager

class BeansDefine(
    val contextDelegate: AppContextDelegate,
    val beansDefinition: BeansDefine.() -> Unit,
) {

    inline fun <reified T : Any> singleton(noinline beanCreator: BeansDefine.() -> T) {
        contextDelegate.registerBean(
            BeanDescriptor(T::class),
            BeanManager(T::class, this, beanCreator),
        )
    }

    inline fun <reified T : Any> bean(): T = contextDelegate.resolveBean(BeanDescriptor(T::class))

    fun eval() {
        apply { beansDefinition() }
    }
}