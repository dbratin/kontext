package org.kontext.appdefine.context

import org.junit.jupiter.api.Test
import org.kontext.appdefine.context.impl.AppContextDefaultBuilder
import org.kontext.appdefine.generated.huge1000.HugeAppDefinition1000
import java.time.Duration
import java.time.temporal.ChronoUnit

class HugeAppContextTest {
    @Test
    fun huge1000() {
        val contextBuilder = AppContextDefaultBuilder()
        val start = System.nanoTime()
        HugeAppDefinition1000(contextBuilder).eval()
        val stop = System.nanoTime()

        println("result ${stop - start}")
        println("result ${Duration.of(stop - start, ChronoUnit.NANOS).toMillis()}")
    }
}