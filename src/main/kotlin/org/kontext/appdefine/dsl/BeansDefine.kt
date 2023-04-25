package org.kontext.appdefine.dsl

import org.kontext.appdefine.context.AppContextBuilder
import org.kontext.appdefine.context.BeanDescriptor
import org.kontext.appdefine.dsl.impl.BeanManagerPrototypeDsl
import org.kontext.appdefine.dsl.impl.BeanManagerSingletonDsl
import org.kontext.appdefine.dsl.impl.BeanManagerThreadConfinedDsl

class BeansDefine(
    val contextDelegate: AppContextBuilder,
    val beansDefinition: BeansDefine.() -> Unit,
) {

    inline fun <reified T : Any> singleton(noinline beanCreator: BeansDefine.() -> T) {
        contextDelegate.registerBean(
            BeanDescriptor(T::class),
            BeanManagerSingletonDsl(T::class, this, beanCreator),
        )
    }

    inline fun <reified T : Any> prototype(noinline beanCreator: BeansDefine.() -> T) {
        contextDelegate.registerBean(
            BeanDescriptor(T::class),
            BeanManagerPrototypeDsl( this, beanCreator),
        )
    }

    inline fun <reified T : Any> threadConfined(noinline beanCreator: BeansDefine.() -> T) {
        contextDelegate.registerBean(
            BeanDescriptor(T::class),
            BeanManagerThreadConfinedDsl(T::class, this, beanCreator),
        )
    }

    inline fun <reified T : Any> bean(): T = contextDelegate.resolveBean(BeanDescriptor(T::class))

    fun eval() {
        apply { beansDefinition() }
    }
}