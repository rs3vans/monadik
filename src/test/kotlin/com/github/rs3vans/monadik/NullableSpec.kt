package com.github.rs3vans.monadik

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class NullableSpec : Spek({
    describe("map") {
        it("should transform") {
            val x = 1.map { 2 }
            assertThat(x, present(equalTo(2)))
        }

        it("should NOT transform") {
            val y: Int? = null
            val x = y.map { 2 }
            assertThat(x, absent())
        }
    }

    describe("filter") {
        it("should pass filter") {
            val x = 1.filter { this == 1 }
            assertThat(x, present(equalTo(1)))
        }

        it("should fail filter") {
            val x = 1.filter { this == 2 }
            assertThat(x, absent())
        }

        it("should skip filter") {
            val y: Int? = null
            val x = y.filter { this == 2 }
            assertThat(x, absent())
        }
    }

    describe("fold operations") {
        it("should fold left") {
            val x = 1
            x.fold({ return@it }, {})
            assertThat("failure", 1, absent())
        }

        it("should fold right") {
            val x: Int? = null
            x.fold({}, { return@it })
            assertThat("failure", 1, absent())
        }

        it("should be present") {
            val x = 1
            x.ifPresent { return@it }
            assertThat("failure", 1, absent())
        }

        it("should be absent") {
            val x: Int? = null
            x.ifAbsent { return@it }
            assertThat("failure", 1, absent())
        }
    }

    describe("or operations") {
        it("should NOT substitute") {
            val x = 1
            val y = x.orElse(2)
            assertThat(y, equalTo(1))
        }

        it("should NOT call substitute") {
            val x = 1
            val y = x.orElse { 2 }
            assertThat(y, equalTo(1))
        }

        it("should NOT throw") {
            val x = 1
            val y = x.orElseThrow { IllegalArgumentException() }
            assertThat(y, equalTo(1))
        }

        it("should substitute") {
            val x: Int? = null
            val y = x.orElse(2)
            assertThat(y, equalTo(2))
        }

        it("should call substitute") {
            val x: Int? = null
            val y = x.orElse { 2 }
            assertThat(y, equalTo(2))
        }

        it("should throw") {
            val x: Int? = null
            assertThat(
                    { x.orElseThrow { IllegalArgumentException() } },
                    throws(isA<IllegalArgumentException>())
            )
        }
    }

    describe("to either operations") {

    }

    describe("to iterable operations") {
        it("should produce a List") {
            val x = 1
            val l = x.toSingletonListOrEmpty()
            assertThat(l, hasSize(equalTo(1)))
        }

        it("should produce an empty List") {
            val x: Int? = null
            val l = x.toSingletonListOrEmpty()
            assertThat(l, hasSize(equalTo(0)))
        }

        it("should produce a Set") {
            val x = 1
            val s = x.toSingletonSetOrEmpty()
            assertThat(s, hasSize(equalTo(1)))
        }

        it("should produce an empty Set") {
            val x: Int? = null
            val s = x.toSingletonSetOrEmpty()
            assertThat(s, hasSize(equalTo(0)))
        }

        it("should produce a Sequence") {
            val x = 1
            val s = x.toSingletonSequenceOrEmpty()
            assertThat(s.toMutableList(), hasSize(equalTo(1)))
        }

        it("should produce an empty Sequence") {
            val x: Int? = null
            val s = x.toSingletonSequenceOrEmpty()
            assertThat(s.toMutableList(), hasSize(equalTo(0)))
        }
    }
})