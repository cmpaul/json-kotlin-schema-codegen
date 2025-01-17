/*
 * TestMultipleOf.java
 *
 * This code was generated by json-kotlin-schema-codegen - JSON Schema Code Generator
 * See https://github.com/pwall567/json-kotlin-schema-codegen
 *
 * It is not advisable to modify generated code as any modifications will be lost
 * when the generation process is re-run.
 */
package net.pwall.json.schema.codegen.test.java;

import java.math.BigDecimal;

/**
 * Test multipleOf
 */
public class TestMultipleOf {

    private static final BigDecimal cg_dec0 = new BigDecimal("0.01");

    private final BigDecimal aaa;
    private final long bbb;
    private final int ccc;

    public TestMultipleOf(
            BigDecimal aaa,
            long bbb,
            int ccc
    ) {
        if (aaa == null)
            throw new IllegalArgumentException("Must not be null - aaa");
        if (aaa.remainder(cg_dec0).compareTo(BigDecimal.ZERO) != 0)
            throw new IllegalArgumentException("aaa not a multiple of 0.01 - " + aaa);
        this.aaa = aaa;
        if (bbb % 100L != 0)
            throw new IllegalArgumentException("bbb not a multiple of 100 - " + bbb);
        this.bbb = bbb;
        if (ccc < 0 || ccc > 40000)
            throw new IllegalArgumentException("ccc not in range 0..40000 - " + ccc);
        if (ccc % 10 != 0)
            throw new IllegalArgumentException("ccc not a multiple of 10 - " + ccc);
        this.ccc = ccc;
    }

    public BigDecimal getAaa() {
        return aaa;
    }

    public long getBbb() {
        return bbb;
    }

    public int getCcc() {
        return ccc;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof TestMultipleOf))
            return false;
        TestMultipleOf typedOther = (TestMultipleOf)other;
        if (!aaa.equals(typedOther.aaa))
            return false;
        if (bbb != typedOther.bbb)
            return false;
        return ccc == typedOther.ccc;
    }

    @Override
    public int hashCode() {
        int hash = aaa.hashCode();
        hash ^= (int)bbb;
        return hash ^ ccc;
    }

    public static class Builder {

        private BigDecimal aaa;
        private long bbb;
        private int ccc;

        public Builder withAaa(BigDecimal aaa) {
            this.aaa = aaa;
            return this;
        }

        public Builder withBbb(long bbb) {
            this.bbb = bbb;
            return this;
        }

        public Builder withCcc(int ccc) {
            this.ccc = ccc;
            return this;
        }

        public TestMultipleOf build() {
            return new TestMultipleOf(
                    aaa,
                    bbb,
                    ccc
            );
        }

    }

}
