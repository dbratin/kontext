package org.kontext.appdefine.dsl

import org.kontext.appdefine.context.AppContextBuilder
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

open class AppDefine(private val contextBuilder: AppContextBuilder, val appDefinition: AppDefine.() -> Unit) {

    private val importedDefines : MutableList<AppDefine> = ArrayList()
    private val beansDefines : MutableList<BeansDefine> = ArrayList()

    fun import(vararg kClasses: KClass<out AppDefine>) {
        for(kClass in kClasses){
            kClass.constructors.find { c ->
                if (c.parameters.size == 1) {
                    (c.parameters[0].type.classifier as KClass<*>).isSuperclassOf(AppContextBuilder::class)
                } else {
                    false
                }
            }?.apply {
                importedDefines.add(call(contextBuilder))
            }
        }
    }

    fun beans(beansDefinition: BeansDefine.() -> Unit) {
        beansDefines.add(BeansDefine(contextBuilder, beansDefinition))
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