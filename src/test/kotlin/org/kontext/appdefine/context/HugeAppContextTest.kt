package org.kontext.appdefine.context

import org.junit.jupiter.api.Test

class HugeAppContextTest {
    @Test
    fun huge8000() {
        val context = AppContext()
        val start = System.nanoTime()
        //HugeAppDefinition8000(context).interpretDefinition()
        val stop = System.nanoTime()

        println("result ${stop - start}")
    }
}