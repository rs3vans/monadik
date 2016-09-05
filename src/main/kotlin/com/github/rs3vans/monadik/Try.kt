package com.github.rs3vans.monadik

sealed class Try<out T : Any> {
    abstract val value: T?
    abstract val exception: Exception?

    operator fun component1() = value
    operator fun component2() = exception

    operator fun unaryPlus() = this is Success
    operator fun not() = this is Failure
    operator fun unaryMinus() = not()

    class Success<out T : Any>(override val value: T) : Try<T>() {
        override val exception = null
    }

    class Failure(override val exception: Exception) : Try<Nothing>() {
        override val value = null
    }

    inline fun <U : Any> flatMap(fn: (T) -> Try<U>): Try<U> = when (this) {
        is Try.Success -> fn(value)
        is Try.Failure -> this
    }

    inline fun <U : Any> map(fn: (T) -> U): Try<U> = flatMap { t -> Try<U> { fn(t) } }

    inline fun fold(left: (T) -> Unit, right: (Exception) -> Unit): Try<T> = apply {
        when (this) {
            is Success -> left(value)
            is Failure -> right(exception)
        }
    }

    inline fun ifSuccess(left: (T) -> Unit): Try<T> = fold(left, {})

    inline fun ifFailure(right: (Exception) -> Unit): Try<T> = fold({}, right)

    companion object {

        inline operator fun <T : Any> invoke(t: () -> T): Try<T> = try {
            Success(t())
        } catch (e: Exception) {
            Failure(e)
        }
    }
}

inline fun <T : Any> Try<T>.orElseGet(t: () -> T): T = when (this) {
    is Try.Success -> value
    is Try.Failure -> t()
}

fun <T : Any> Try<T>.orElse(t: T): T = orElseGet { t }

inline fun <T : Any> Try<T>.orElseThrow(otherValue: (Exception) -> Exception): T = when (this) {
    is Try.Success -> value
    is Try.Failure -> throw otherValue(exception)
}
