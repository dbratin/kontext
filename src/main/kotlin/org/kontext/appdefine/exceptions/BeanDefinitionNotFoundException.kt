package org.kontext.appdefine.exceptions

import org.kontext.appdefine.context.BeanDescriptor

class BeanDefinitionNotFoundException(val beanDescriptor: BeanDescriptor<*>) : Exception()