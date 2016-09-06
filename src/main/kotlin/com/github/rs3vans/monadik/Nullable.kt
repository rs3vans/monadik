package com.github.rs3vans.monadik

inline fun <T : Any, U : Any> T?.map(transformer: T.() -> U?): U? = if (this != null) transformer(this) else null

inline fun <T : Any> T?.filter(predicate: T.() -> Boolean): T? = if (this != null && predicate(this)) this else null

inline fun <T : Any> T?.fold(left: T.() -> Unit, right: () -> Unit): T? = this.apply { if (this != null) left() else right() }

inline fun <T : Any> T?.ifPresent(left: T.() -> Unit): T? = fold(left, {})

inline fun <T : Any> T?.ifAbsent(right: () -> Unit): T? = fold({}, right)

inline fun <T : Any> T?.orElse(other: () -> T): T = this ?: other()

fun <T : Any> T?.orElse(other: T): T = orElse { other }

inline fun <T : Any> T?.orElseThrow(exception: () -> Exception): T = orElse { throw exception() }

fun <T : Any, U : Any> T?.toLeft(right: U): Either<T, U> = if (this != null) Either.Left(this) else Either.Right(right)

fun <T : Any, U : Any> T?.toRight(left: U): Either<U, T> = if (this != null) Either.Right(this) else Either.Left(left)

fun <T : Any> T?.toTry(exception: () -> Exception = { NullPointerException() }): Try<T> =
        map { Try.Success(this) } ?: Try.Failure(exception())

fun <T : Any> T?.toSingletonListOrEmpty(): List<T> = map { listOf(this) } ?: emptyList()

fun <T : Any> T?.toSingletonSetOrEmpty(): Set<T> = map { setOf(this) } ?: emptySet()

fun <T : Any> T?.toSingletonSequenceOrEmpty(): Sequence<T> = map { sequenceOf(this) } ?: emptySequence()