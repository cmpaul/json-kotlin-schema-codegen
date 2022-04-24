/*
 * TestMultipleOf.kt
 *
 * This code was generated by json-kotlin-schema-codegen - JSON Schema Code Generator
 * See https://github.com/pwall567/json-kotlin-schema-codegen
 *
 * It is not advisable to modify generated code as any modifications will be lost
 * when the generation process is re-run.
 */
package net.pwall.json.schema.codegen.test.kotlin

import java.math.BigDecimal

/**
 * Test multipleOf
 */
data class TestMultipleOf(
    val aaa: BigDecimal,
    val bbb: Long,
    val ccc: Int
) {

    init {
        require(aaa.rem(cg_dec0).compareTo(BigDecimal.ZERO) == 0) { "aaa not a multiple of 0.01 - $aaa" }
        require(bbb.rem(100L) == 0L) { "bbb not a multiple of 100 - $bbb" }
        require(ccc in 0..40000) { "ccc not in range 0..40000 - $ccc" }
        require(ccc.rem(10) == 0) { "ccc not a multiple of 10 - $ccc" }
    }

    companion object {
        private val cg_dec0 = BigDecimal("0.01")
    }

}
