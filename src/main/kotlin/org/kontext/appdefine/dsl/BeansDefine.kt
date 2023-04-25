package org.kontext.appdefine.dsl

import org.kontext.appdefine.context.AppContextBuilder
import org.kontext.appdefine.context.BeanDescriptor
import org.kontext.appdefine.context.impl.BeanManagerDefault
import org.kontext.appdefine.dsl.impl.BeanManagerDsl

class BeansDefine(
    val contextDelegate: AppContextBuilder,
    val beansDefinition: BeansDefine.() -> Unit,
) {

    inline fun <reified T : Any> singleton(noinline beanCreator: BeansDefine.() -> T) {
        contextDelegate.registerBean(
            BeanDescriptor(T::class),
            BeanManagerDsl(T::class, this, beanCreator),
        )
    }

    inline fun <reified T : Any> bean(): T = contextDelegate.resolveBean(BeanDescriptor(T::class))

    fun eval() {
        apply { beansDefinition() }
    }
}