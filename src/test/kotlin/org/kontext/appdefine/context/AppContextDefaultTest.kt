package org.kontext.appdefine.context

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.kontext.appdefine.context.impl.AppContextDefault
import org.kontext.appdefine.context.impl.BeanManagerSingletonDefault
import org.kontext.appdefine.exceptions.AppContextDestroyedException
import org.kontext.appdefine.exceptions.BeanNotFoundException

class AppContextDefaultTest {

    @Test
    fun addNewSingletonBean() {
        val context = AppContextDefault()
        context.registerSingleton(
            BeanDescriptor(BeanOne::class),
            BeanManagerSingletonDefault(BeanOne::class, { BeanOne() })
        )
        context.registerSingleton(
            BeanDescriptor(BeanTwo::class),
            BeanManagerSingletonDefault(BeanTwo::class, { BeanTwo() })
        )
        context.start()

        context.getBean(BeanOne::class) shouldBe BeanOne()
        context.getBean(BeanTwo::class) shouldBe BeanTwo()
    }

    @Test
    fun getNoneExistingBean() {
        val context = AppContextDefault()

        shouldThrow<BeanNotFoundException> {
            context.getBean(BeanOne::class)
        }
    }

    @Test
    fun destroyContext() {
        val context = AppContextDefault()

        context.registerSingleton(
            BeanDescriptor(BeanOne::class),
            BeanManagerSingletonDefault(BeanOne::class, { BeanOne() })
        )
        context.registerSingleton(
            BeanDescriptor(BeanTwo::class),
            BeanManagerSingletonDefault(BeanTwo::class, { BeanTwo() })
        )
        context.registerSingleton(
            BeanDescriptor(DestroyableBean::class),
            BeanManagerSingletonDefault(DestroyableBean::class, { DestroyableBean() }, { destroyIt() })
        )
        context.start()

        val destroyableBean = context.getBean(DestroyableBean::class)

        context.destroy()

        destroyableBean.destroyed shouldBe true

        shouldThrow<AppContextDestroyedException> {
            context.registerSingleton(
                BeanDescriptor(BeanTwo::class),
                BeanManagerSingletonDefault(BeanTwo::class, { BeanTwo() })
            )
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