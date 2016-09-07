package com.github.rs3vans.monadik

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TrySpec : Spek({
    describe("operators") {
        it("should return false for not()") {
            val x = Try<Int> { 1 }
            assertThat(!x, equalTo(false))
        }

        it("should return true for not()") {
            val x = Try<Int> { throw IllegalStateException() }
            assertThat(!x, equalTo(true))
        }
    }

    describe("construction") {
        it("should be a Success") {
            val x = Try<Int> { 1 }
            assertThat(x, isA<Try.Success<*>>())
            assertThat(x.isSuccess, equalTo(true))
            assertThat(x.value, equalTo(1))
        }

        it("should be a Failure") {
            val x = Try<Int> { throw IllegalStateException() }
            assertThat(x, isA<Try.Failure>())
            assertThat({ x.value }, throws(isA<IllegalStateException>()))
        }
    }

    describe("flatMap") {
        it("should transform to a Success") {
            val x = Try<Int> { 1 }
            val y = x.flatMap { Try<String> { "" } }
            assertThat(y.isSuccess, equalTo(true))
        }

        it("should transform to a Failure") {
            val x = Try<Int> { throw IllegalStateException() }
            val y = x.flatMap { Try<String> { "" } }
            assertThat(y.isFailure, equalTo(true))
        }
    }

    describe("map") {
        it("should transform to a Success") {
            val x = Try<Int> { 1 }
            val y = x.map { "" }
            assertThat(y.isSuccess, equalTo(true))
        }

        it("should transform to a Failure") {
            val x = Try<Int> { throw IllegalStateException() }
            val y = x.map { "" }
            assertThat(y.isFailure, equalTo(true))
        }
    }

    describe("fold operations") {
        it("should fold left") {
            val x = Try<Int> { 1 }
            x.fold({ return@it }, {})
            assertThat("failure", false, equalTo(true))
        }

        it("should fold right") {
            val x = Try<Int> { throw IllegalStateException() }
            x.fold({}, { return@it })
            assertThat("failure", false, equalTo(true))
        }

        it("should execute success") {
            val x = Try<Int> { 1 }
            x.ifSuccess { return@it }
            assertThat("failure", false, equalTo(true))
        }

        it("should execute failure") {
            val x = Try<Int> { throw IllegalStateException() }
            x.ifFailure { return@it }
            assertThat("failure", false, equalTo(true))
        }
    }

    describe("destructuring") {
        it("should destructure a Success") {
            val (v, e) = Try<Int> { 1 }
            assertThat(v, present(equalTo(1)))
            assertThat(e, absent())
        }

        it("should destructure a Failure") {
            val (v, e) = Try<Int> { throw IllegalStateException() }
            assertThat(v, absent())
            assertThat(e, present(isA<IllegalStateException>()))
        }
    }

    describe("flatten") {
        it("should flatten nested Success") {
            val x = Try<Try<Int>> { Try<Int> { 1 } }
            val y = x.flatten()
            assertThat(y, isA<Try.Success<Int>>())
            assertThat(y.value, equalTo(1))
        }

        it("should NOT flatten Failure") {
            val x = Try<Try<Int>> { throw IllegalStateException() }
            val y = x.flatten()
            assertThat(y, isA<Try.Failure>())
        }

        it("should return true for contains() as Success") {
            val x = Try<Int> { 1 }
            assertThat(1 in x, equalTo(true))
        }

        it("should return false for contains() as Failure") {
            val x = Try<Int> { throw IllegalStateException() }
            assertThat(1 in x, equalTo(false))
        }
    }

    describe("or substitutions") {
        it("should NOT substitute") {
            val x = Try<Int> { 1 }
            val y = x.orElse(2)
            assertThat(y, equalTo(1))
        }

        it("should NOT call substitute") {
            val x = Try<Int> { 1 }
            val y = x.orElseGet { 2 }
            assertThat(y, equalTo(1))
        }

        it("should substitute") {
            val x = Try<Int> { throw IllegalStateException() }
            val y = x.orElse(2)
            assertThat(y, equalTo(2))
        }

        it("should call substitute") {
            val x = Try<Int> { throw IllegalStateException() }
            val y = x.orElseGet { 2 }
            assertThat(y, equalTo(2))
        }
    }
})