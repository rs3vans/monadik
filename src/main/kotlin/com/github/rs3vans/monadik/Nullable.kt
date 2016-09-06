package com.github.rs3vans.monadik

inline fun <T : Any, U : Any> T?.map(transformer: T.() -> U?): U? = if (this != null) transformer(this) else null

inline fun <T : Any> T?.filter(predicate: T.() -> Boolean): T? = if (this != null && predicate(this)) this else null

inline fun <T : Any> T?.or(other: () -> T): T = this ?: other()

fun <T : Any> T?.or(other: T): T = or { other }

fun <T : Any, U : Any> T?.toLeft(right: U): Either<T, U> = if (this != null) Either.Left(this) else Either.Right(right)

fun <T : Any, U : Any> T?.toRight(left: U): Either<U, T> = if (this != null) Either.Right(this) else Either.Left(left)

fun <T : Any> T?.toTry(exception: () -> Exception = { NullPointerException() }): Try<T> =
        map { Try.Success(this) } ?: Try.Failure(exception())