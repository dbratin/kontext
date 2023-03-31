package org.kontext.appdefine.context

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.kontext.appdefine.exceptions.AppContextDestroyedException
import org.kontext.appdefine.exceptions.BeanNotFoundException

class AppContextTest {

    @Test
    fun addNewSingletonBean() {
        val context = AppContext()
        val beanOne = BeanOne()
        val beanTwo = BeanTwo()
        context.addSingleton(beanOne)
        context.addSingleton(beanTwo)
        context.getBean(BeanOne::class) shouldBe beanOne
        context.getBean(BeanTwo::class) shouldBe beanTwo
    }

    @Test
    fun getNoneExistingBean() {
        val context = AppContext()

        shouldThrow<BeanNotFoundException> {
            context.getBean(BeanOne::class)
        }
    }

    @Test
    fun destroyContext() {
        val context = AppContext()
        val destroyableBean = DestroyableBean()

        context.addSingleton(BeanOne())
        context.addSingleton(BeanTwo())
        context.addSingleton(BeanDescriptor(DestroyableBean::class) { destroyIt() }, destroyableBean)
        context.destroy()

        destroyableBean.destroyed shouldBe true

        shouldThrow<AppContextDestroyedException> {
            context.addSingleton(BeanTwo::class)
        }
        shouldThrow<AppContextDestroyedException> {
            context.findBean(BeanTwo::class)
        }
        shouldThrow<AppContextDestroyedException> {
            context.getBean(BeanOne::class)
        }
        shouldThrow<AppContextDestroyedException> {
            context.destroy()
        }
    }

    data class BeanOne(val name: String = "beanOne")

    data class BeanTwo(val name: String = "beanTwo")

    data class DestroyableBean(
        val name: String = "destroyableBean",
        var destroyed: Boolean = false,
    ) {
        fun destroyIt() {
            destroyed = true
        }
    }
}