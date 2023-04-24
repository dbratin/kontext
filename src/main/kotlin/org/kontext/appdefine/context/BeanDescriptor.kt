package org.kontext.appdefine.context

import kotlin.reflect.KClass
import kotlin.reflect.cast

data class BeanDescriptor<T : Any>(
    val kClass: KClass<T>,
)