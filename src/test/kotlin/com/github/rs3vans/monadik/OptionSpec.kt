package com.github.rs3vans.monadik

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class OptionSpec : Spek({
    describe("flatMap") {
        it("should transform") {
            val x = Option(1)
            val y = x.flatMap { Option(2) }
            assertThat(y.present, equalTo(true))
            assertThat(y.value, equalTo(2))
        }

        it("should NOT transform") {
            val x = Option(null)
            val y = x.flatMap { Option(2) }
            assertThat(y.absent, equalTo(true))
            assertThat({ y.value }, throws(isA<NullPointerException>()))
        }
    }

    describe("map") {
        it("should transform") {
            val x = Option(1)
            val y = x.map { 2 }
            assertThat(y.present, equalTo(true))
            assertThat(y.value, equalTo(2))
        }

        it("should NOT transform") {
            val x = Option(null)
            val y = x.map { 2 }
            assertThat(y.absent, equalTo(true))
            assertThat({ y.value }, throws(isA<NullPointerException>()))
        }
    }

    describe("filter") {
        it("should pass filter") {
            val x = Option(1)
            val y = x.filter { it == 1 }
            assertThat(y.present, equalTo(true))
            assertThat(y.value, equalTo(1))
        }

        it("should fail filter") {
            val x = Option(1)
            val y = x.filter { it == 2 }
            assertThat(y.absent, equalTo(true))
            assertThat({ y.value }, throws(isA<NullPointerException>()))
        }

        it("should skip filter") {
            val x = Option(null)
            val y = x.map { 2 }
            assertThat(y.absent, equalTo(true))
            assertThat({ y.value }, throws(isA<NullPointerException>()))
        }
    }

    describe("fold operations") {
        it("should fold left") {
            val x = Option(1)
            x.fold({ return@it }, {})
            assertThat("failure", 1, absent())
        }

        it("should fold right") {
            val x = Option(null)
            x.fold({}, { return@it })
            assertThat("failure", 1, absent())
        }

        it("should be present") {
            val x = Option(1)
            x.ifPresent { return@it }
            assertThat("failure", 1, absent())
        }

        it("should be absent") {
            val x = Option(null)
            x.ifAbsent { return@it }
            assertThat("failure", 1, absent())
        }
    }

    describe("or operations") {
        it("should NOT substitute") {
            val x = Option(1)
            val y = x.orElse(2)
            assertThat(y, equalTo(1))
        }

        it("should NOT call substitute") {
            val x = Option(1)
            val y = x.orElseGet { 2 }
            assertThat(y, equalTo(1))
        }

        it("should NOT throw") {
            val x = Option(1)
            val y = x.orElseThrow { IllegalArgumentException() }
            assertThat(y, equalTo(1))
        }

        it("should substitute") {
            val x = Option(null)
            val y = x.orElse(2)
            assertThat(y, equalTo(2))
        }

        it("should call substitute") {
            val x = Option(null)
            val y = x.orElseGet { 2 }
            assertThat(y, equalTo(2))
        }

        it("should throw") {
            val x = Option(null)
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
            val x = Option(1)
            val l = x.toList()
            assertThat(l, hasSize(equalTo(1)))
        }

        it("should produce an empty List") {
            val x = Option(null)
            val l = x.toList()
            assertThat(l, hasSize(equalTo(0)))
        }

        it("should produce a Set") {
            val x = Option(1)
            val s = x.toSet()
            assertThat(s, hasSize(equalTo(1)))
        }

        it("should produce an empty Set") {
            val x = Option(null)
            val s = x.toSet()
            assertThat(s, hasSize(equalTo(0)))
        }

        it("should produce a Sequence") {
            val x = Option(1)
            val s = x.toSequence()
            assertThat(s.toMutableList(), hasSize(equalTo(1)))
        }

        it("should produce an empty Set") {
            val x = Option(null)
            val s = x.toSet()
            assertThat(s, hasSize(equalTo(0)))
        }
    }
})