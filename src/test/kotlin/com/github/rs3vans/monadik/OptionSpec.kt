package com.github.rs3vans.monadik

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class OptionSpec : Spek({
    describe("flatMap") {
        it("should transform") {
            val x: Int? = 1
            val y = x.flatMap {
                val z: Int? = 2
                z
            }
            assertThat(y, present(equalTo(2)))
        }

        it("should NOT transform") {
            val x: Int? = null
            val y = x.flatMap {
                val z: Int? = 2
                z
            }
            assertThat(y, absent())
        }
    }

    describe("map") {
        it("should transform") {
            val x: Int? = 1
            val y = x.map { 2 }
            assertThat(y, present(equalTo(2)))
        }

        it("should NOT transform") {
            val x: Int? = null
            val y = x.map { 2 }
            assertThat(y, absent())
        }
    }

    describe("filter") {
        it("should pass filter") {
            val x: Int? = 1
            val y = x.filter { it == 1 }
            assertThat(y, present(equalTo(1)))
        }

        it("should fail filter") {
            val x: Int? = 1
            val y = x.filter { it == 2 }
            assertThat(y, absent())
        }

        it("should skip filter") {
            val x: Int? = null
            val y = x.map { 2 }
            assertThat(y, absent())
        }
    }

    describe("fold operations") {
        it("should fold left") {
            val x: Int? = 1
            x.fold({ return@it }, {})
            assertThat("failure", x, absent())
        }

        it("should fold right") {
            val x: Int? = null
            x.fold({}, { return@it })
            assertThat("failure", x, present())
        }

        it("should be present") {
            val x: Int? = 1
            x.ifPresent { return@it }
            assertThat("failure", x, absent())
        }

        it("should be absent") {
            val x: Int? = null
            x.ifAbsent { return@it }
            assertThat("failure", x, present())
        }
    }
})