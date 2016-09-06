package com.github.rs3vans.monadik

sealed class Option<out T : Any> {
    
    abstract val value: T

    val present: Boolean = this is Some
    val absent: Boolean = this is None
    
    operator fun not() = absent

    class Some<out T : Any>(override val value: T) : Option<T>()
    
    object None : Option<Nothing>() {
        override val value: Nothing
            get() = throw NullPointerException()
    }

    inline fun <U : Any> flatMap(transformer: (T) -> Option<U>): Option<U> = when (this) {
        is Some -> transformer(value)
        is None -> this
    }

    inline fun <U : Any> map(transformer: (T) -> U): Option<U> = flatMap { Option(transformer(it)) }

    inline fun filter(predicate: (T) -> Boolean): Option<T> = flatMap { if (predicate(it)) this else None }

    inline fun fold(left: (T) -> Unit, right: () -> Unit): Option<T> = apply {
        when (this) {
            is Some -> left(value)
            is None -> right()
        }
    }

    inline fun ifPresent(left: (T) -> Unit): Option<T> = fold(left, {})

    inline fun ifAbsent(right: () -> Unit): Option<T> = fold({}, right)

    fun <U : Any> toLeft(right: U): Either<T, U> = when (this) {
        is Some -> Either.Left(value)
        is None -> Either.Right(right)
    }

    fun <U : Any> toRight(left: U): Either<U, T> = when (this) {
        is Some -> Either.Right(value)
        is None -> Either.Left(left)
    }

    fun toTry(exception: () -> Exception = { NullPointerException() }): Try<T> = when (this) {
        is Some -> Try.Success(value)
        is None -> Try.Failure(exception())
    }

    companion object {

        operator fun <T : Any> invoke(value: T?): Option<T> = if (value != null) {
            Some(value)
        } else {
            None
        }
    }
}

inline fun <T : Any> Option<T>.orElseGet(other: () -> T): T = when (this) {
    is Option.Some -> value
    is Option.None -> other()
}

fun <T : Any> Option<T>.orElse(other: T): T = orElseGet { other }

inline fun <T : Any> Option<T>.orElseThrow(exception: () -> Exception): T = orElseGet { throw exception() }

fun <T : Any> Option<T>.toList(): List<T> = map { listOf(it) }.orElseGet { emptyList() }

fun <T : Any> Option<T>.toSet(): Set<T> = map { setOf(it) }.orElseGet { emptySet() }

fun <T : Any> Option<T>.toSequence(): Sequence<T> = map { sequenceOf(it) }.orElseGet { emptySequence() }