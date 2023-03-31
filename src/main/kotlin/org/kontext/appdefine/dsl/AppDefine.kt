package org.kontext.appdefine.dsl

import org.kontext.appdefine.context.AppContext

class AppDefine(val context: AppContext, val appDefinition: AppDefine.() -> Unit) {

    private var beansDefine: BeansDefine? = null
    fun beans(beansDefinition: BeansDefine.() -> Unit) {
        beansDefine = BeansDefine(context, beansDefinition)
    }

    fun interpretDefinition() {
        this.appDefinition()
    }
}