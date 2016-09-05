package com.github.rs3vans.monadik

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import com.natpryce.hamkrest.present
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TrySpec : Spek({
    describe("a successful Try operation") {
        it("should be a Success") {
            val x = Try<Int> { 1 }
            assertThat(x, isA<Try.Success<*>>())
            assertThat(+x, equalTo(true))
            assertThat(!x, equalTo(false))
            assertThat(x.value!!, equalTo(1))

            val (v, e) = x
            assertThat(v, present(equalTo(1)))
            assertThat(e, absent())
        }
    }

    describe("a failure Try operation") {
        it("should be a Failure") {
            val x = Try<Int> {
                throw IllegalStateException()
            }
            assertThat(x, isA<Try.Failure>())
            assertThat(+x, equalTo(false))
            assertThat(!x, equalTo(true))
            assertThat(x.exception!!, isA<IllegalStateException>())
        }
    }
})