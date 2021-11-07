/*
 * @(#) CodeGeneratorComplexBaseAndDerivedClassTest.kt
 *
 * json-kotlin-schema-codegen  JSON Schema Code Generation
 * Copyright (c) 2021 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json.schema.codegen

import kotlin.test.Test
import kotlin.test.expect
import java.io.File
import java.io.StringWriter
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.OutputDetails
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.createHeader
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.dirs
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.outputCapture

class CodeGeneratorComplexBaseAndDerivedClassTest {

    @Test fun `should generate complex base and derived classes`() {
        val inputA = File("src/test/resources/test-complex-base.schema.json")
        val inputB = File("src/test/resources/test-complex-base-derived.schema.json")
        val codeGenerator = CodeGenerator()
        val stringWriterA = StringWriter()
        val outputDetailsA = OutputDetails(TargetFileName("TestComplexBase", "kt", dirs), stringWriterA)
        val stringWriterB = StringWriter()
        val outputDetailsB = OutputDetails(TargetFileName("TestComplexBaseDerived", "kt", dirs), stringWriterB)
        codeGenerator.basePackageName = "com.example"
        codeGenerator.outputResolver = outputCapture(outputDetailsA, outputDetailsB)
        codeGenerator.generate(inputA, inputB)
        expect(createHeader("TestComplexBase.kt") + expectedA) { stringWriterA.toString() }
        expect(createHeader("TestComplexBaseDerived.kt") + expectedB) { stringWriterB.toString() }
    }

    @Test fun `should generate complex base and derived classes in Java`() {
        val inputA = File("src/test/resources/test-complex-base.schema.json")
        val inputB = File("src/test/resources/test-complex-base-derived.schema.json")
        val codeGenerator = CodeGenerator(TargetLanguage.JAVA)
        val stringWriterA = StringWriter()
        val outputDetailsA = OutputDetails(TargetFileName("TestComplexBase", "java", dirs), stringWriterA)
        val stringWriterB = StringWriter()
        val outputDetailsB = OutputDetails(TargetFileName("TestComplexBaseDerived", "java", dirs), stringWriterB)
        codeGenerator.basePackageName = "com.example"
        codeGenerator.outputResolver = outputCapture(outputDetailsA, outputDetailsB)
        codeGenerator.generate(inputA, inputB)
        expect(createHeader("TestComplexBase.java") + expectedAJava) { stringWriterA.toString() }
        expect(createHeader("TestComplexBaseDerived.java") + expectedBJava) { stringWriterB.toString() }
    }

    companion object {

        const val expectedA =
"""package com.example

/**
 * Test complex base class.
 */
open class TestComplexBase(
    val aaa: Aaa,
    val qqq: String
) {

    init {
        require(qqq.isNotEmpty()) { "qqq length < minimum 1 - ${'$'}{qqq.length}" }
    }

    override fun equals(other: Any?): Boolean = this === other || other is TestComplexBase &&
            aaa == other.aaa &&
            qqq == other.qqq

    override fun hashCode(): Int =
            aaa.hashCode() xor
            qqq.hashCode()

    data class Aaa(
        val xxx: String
    ) {

        init {
            require(xxx.isNotEmpty()) { "xxx length < minimum 1 - ${'$'}{xxx.length}" }
        }

    }

}
"""

        const val expectedB =
"""package com.example

/**
 * Test complex base derived class.
 */
class TestComplexBaseDerived(
    aaa: Aaa,
    qqq: String,
    val bbb: String
) : TestComplexBase(aaa, qqq) {

    init {
        require(bbb.isNotEmpty()) { "bbb length < minimum 1 - ${'$'}{bbb.length}" }
    }

    override fun equals(other: Any?): Boolean = this === other || other is TestComplexBaseDerived &&
            super.equals(other) &&
            bbb == other.bbb

    override fun hashCode(): Int = super.hashCode() xor
            bbb.hashCode()

}
"""

        const val expectedAJava =
"""package com.example;

/**
 * Test complex base class.
 */
public class TestComplexBase {

    private final Aaa aaa;
    private final String qqq;

    public TestComplexBase(
            Aaa aaa,
            String qqq
    ) {
        if (aaa == null)
            throw new IllegalArgumentException("Must not be null - aaa");
        this.aaa = aaa;
        if (qqq == null)
            throw new IllegalArgumentException("Must not be null - qqq");
        if (qqq.length() < 1)
            throw new IllegalArgumentException("qqq length < minimum 1 - " + qqq.length());
        this.qqq = qqq;
    }

    public Aaa getAaa() {
        return aaa;
    }

    public String getQqq() {
        return qqq;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof TestComplexBase))
            return false;
        TestComplexBase typedOther = (TestComplexBase)other;
        if (!aaa.equals(typedOther.aaa))
            return false;
        return qqq.equals(typedOther.qqq);
    }

    @Override
    public int hashCode() {
        int hash = aaa.hashCode();
        return hash ^ qqq.hashCode();
    }

    public static class Aaa {

        private final String xxx;

        public Aaa(
                String xxx
        ) {
            if (xxx == null)
                throw new IllegalArgumentException("Must not be null - xxx");
            if (xxx.length() < 1)
                throw new IllegalArgumentException("xxx length < minimum 1 - " + xxx.length());
            this.xxx = xxx;
        }

        public String getXxx() {
            return xxx;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (!(other instanceof Aaa))
                return false;
            Aaa typedOther = (Aaa)other;
            return xxx.equals(typedOther.xxx);
        }

        @Override
        public int hashCode() {
            return xxx.hashCode();
        }

    }

}
"""

        const val expectedBJava =
"""package com.example;

/**
 * Test complex base derived class.
 */
public class TestComplexBaseDerived extends TestComplexBase {

    private final String bbb;

    public TestComplexBaseDerived(
            Aaa aaa,
            String qqq,
            String bbb
    ) {
        super(aaa, qqq);
        if (bbb == null)
            throw new IllegalArgumentException("Must not be null - bbb");
        if (bbb.length() < 1)
            throw new IllegalArgumentException("bbb length < minimum 1 - " + bbb.length());
        this.bbb = bbb;
    }

    public String getBbb() {
        return bbb;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof TestComplexBaseDerived))
            return false;
        if (!super.equals(other))
            return false;
        TestComplexBaseDerived typedOther = (TestComplexBaseDerived)other;
        return bbb.equals(typedOther.bbb);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        return hash ^ bbb.hashCode();
    }

}
"""

    }

}