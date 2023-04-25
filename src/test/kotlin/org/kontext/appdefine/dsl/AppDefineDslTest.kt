package org.kontext.appdefine.dsl

import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.kontext.appdefine.context.AppContextBuilder
import org.kontext.appdefine.context.impl.AppContextDefault
import org.kontext.appdefine.context.impl.AppContextDefaultBuilder

class AppDefineDslTest {

    class ImportedDefinition(builder: AppContextBuilder) : AppDefine(builder, {
        beans {
            singleton {
                ImportedSingleton()
            }
        }
    })

    class TestDefinition(builder: AppContextBuilder) : AppDefine(builder, {
        import(ImportedDefinition::class)

        beans {
            singleton {
                TestSingleton(
                    dependency1 = bean(),
                    dependency2 = bean(),
                    dependency3 = bean(),
                    dependency4 = bean(),
                )
            }

            singleton {
                TestPrototypeBean(
                    dependency = bean()
                )
            }

            singleton {
                YetAnotherTestSingleton()
            }

            singleton {
                TestThreadLocalBean(
                    dependency = bean()
                )
            }

            /*
            prototype {
                TestPrototypeBean(
                        dependency = bean<YetAnotherTestSingleton>()
                )
            }

            threadConfined {
                TestThreadLocalBean(
                        dependency = bean<YetAnotherTestSingleton>()
                )
            }
             */
        }
    })

    @Test
    fun applicationDefinitionRun() {
        val builder = AppContextDefaultBuilder()

        TestDefinition(builder).eval()

        val context = builder.build()

        context.findBean(TestSingleton::class) shouldNotBe null
        context.findBean(TestPrototypeBean::class) shouldNotBe null
        context.findBean(YetAnotherTestSingleton::class) shouldNotBe null
        context.findBean(TestThreadLocalBean::class) shouldNotBe null

    }

    data class TestSingleton(
        val dependency1: YetAnotherTestSingleton,
        val dependency2: TestPrototypeBean,
        val dependency3: TestThreadLocalBean,
        val dependency4: ImportedSingleton,
    )

    data class TestThreadLocalBean(
        val dependency: YetAnotherTestSingleton,
    )

    data class TestPrototypeBean(
        val dependency: YetAnotherTestSingleton,
    )

    class YetAnotherTestSingleton

    class ImportedSingleton
}