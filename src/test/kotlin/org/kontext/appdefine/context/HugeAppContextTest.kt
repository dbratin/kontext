package org.kontext.appdefine.context

import org.junit.jupiter.api.Test
import org.kontext.appdefine.context.impl.AppContextDefault

class HugeAppContextTest {
    @Test
    fun huge8000() {
        val context = AppContextDefault()
        val start = System.nanoTime()
        //HugeAppDefinition8000(context).interpretDefinition()
        val stop = System.nanoTime()

        println("result ${stop - start}")
    }
}