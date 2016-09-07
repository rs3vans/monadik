package com.github.rs3vans.monadik

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class EitherSpec : Spek({
    describe("fold operations") {
        it("should fold left") {
            val x: Either<Int, String> = Either.Left(1)
            x.fold({ return@it }, {})
            assertThat("failure", false, equalTo(true))
        }

        it("should fold right") {
            val x: Either<Int, String> = Either.Right("")
            x.fold({}, { return@it })
            assertThat("failure", false, equalTo(true))
        }

        it("should execute left") {
            val x: Either<Int, String> = Either.Left(1)
            x.ifLeft { return@it }
            assertThat("failure", false, equalTo(true))
        }

        it("should execute failure") {
            val x: Either<Int, String> = Either.Right("")
            x.ifRight { return@it }
            assertThat("failure", false, equalTo(true))
        }
    }

    describe("destructuring") {
        it("should destructure a Left") {
            val x: Either<Int, String> = Either.Left(1)
            val (l, r) = x
            assertThat(l, present(equalTo(1)))
            assertThat(r, absent())
        }

        it("should destructure a Right") {
            val x: Either<Int, String> = Either.Right("")
            val (l, r) = x
            assertThat(l, absent())
            assertThat(r, present(isA<String>()))
        }
    }

    describe("flatten operations") {
        it("should flatten nested Left") {
            val x: Either<Either<Int, String>, String> = Either.Left(Either.Left(1))
            val y = x.flattenLeft()
            assertThat(y, isA<Either.Left<Int>>())
            assertThat(y.left.value, equalTo(1))
        }

        it("should flatten nested Right") {
            val x: Either<Int, Either<Int, String>> = Either.Right(Either.Right(""))
            val y = x.flattenRight()
            assertThat(y, isA<Either.Right<String>>())
            assertThat(y.right.value, equalTo(""))
        }
    }

    describe("swap") {
        it("should swap") {
            val x: Either<Int, String> = Either.Left(1)
            val y = x.swap()
            assertThat(y, isA<Either.Right<Int>>())
            assertThat(y.right.value, equalTo(1))
        }
    }
})