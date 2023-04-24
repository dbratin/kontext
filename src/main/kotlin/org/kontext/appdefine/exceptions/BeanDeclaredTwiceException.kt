package org.kontext.appdefine.exceptions

import org.kontext.appdefine.context.BeanDescriptor

class BeanDeclaredTwiceException(val descriptor: BeanDescriptor<*>) : Exception()