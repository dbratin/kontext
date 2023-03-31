package org.kontext.appdefine.context

import kotlin.reflect.KClass
import kotlin.reflect.cast

data class BeanDescriptor<T:Any>(
    val kClass: KClass<T>,
    val destroyMethod: (T.() -> Unit)? = null,
) {
    fun hasDestroyMethod() = destroyMethod != null

    fun applyDestroyMethod(bean: Any) = destroyMethod?.takeIf { kClass.isInstance(bean) }?.invoke(kClass.cast(bean))

}