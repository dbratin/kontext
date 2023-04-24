package org.kontext.appdefine.dsl

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

open class AppDefine(private val contextDelegate: AppContextDelegate, val appDefinition: AppDefine.() -> Unit) {

    private val importedDefines : MutableList<AppDefine> = ArrayList()
    private val beansDefines : MutableList<BeansDefine> = ArrayList()

    fun import(vararg kClasses: KClass<out AppDefine>) {
        for(kClass in kClasses){
            kClass.constructors.find { c ->
                if (c.parameters.size == 1) {
                    val param = c.parameters[0]
                    param.kind == KParameter.Kind.INSTANCE &&
                    param.type.classifier == AppContextDelegate::class
                } else {
                    false
                }
            }?.apply {
                importedDefines.add(call(contextDelegate))
            }
        }
    }

    fun beans(beansDefinition: BeansDefine.() -> Unit) {
        beansDefines.add(BeansDefine(contextDelegate, beansDefinition))
    }

    fun eval() {
        this.appDefinition()
        for(define in importedDefines){
            define.eval()
        }
        for(define in beansDefines) {
            define.eval()
        }
    }
}