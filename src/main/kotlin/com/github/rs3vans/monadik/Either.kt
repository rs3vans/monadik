package com.github.rs3vans.monadik

sealed class Either<out T : Any, out U : Any> {
    abstract val left: Option<T>
    abstract val right: Option<U>

    operator fun component1() = if (this is Left<T>) left.value else null
    operator fun component2() = if (this is Right<U>) right.value else null

    class Left<out T : Any>(left: T) : Either<T, Nothing>() {
        override val left = Option.Some(left)
        override val right = Option.None

        override fun toString() = "Left(${left.value})"
        override fun hashCode() = left.hashCode()
        override fun equals(other: Any?) = other is Left<*> && left.equals(other.left)
    }

    class Right<out U : Any>(right: U) : Either<Nothing, U>() {
        override val left = Option.None
        override val right = Option.Some(right)

        override fun toString() = "Right(${right.value})"
        override fun hashCode() = right.hashCode()
        override fun equals(other: Any?) = other is Right<*> && right.equals(other.right)
    }

    inline fun fold(leftFn: (T) -> Unit, rightFn: (U) -> Unit): Either<T, U> = apply {
        when (this) {
            is Left -> leftFn(left.value)
            is Right -> rightFn(right.value)
        }
    }

    inline fun ifLeft(leftFn: (T) -> Unit): Either<T, U> = fold(leftFn, {})

    inline fun ifRight(rightFn: (U) -> Unit): Either<T, U> = fold({}, rightFn)

    fun swap(): Either<U, T> = when (this) {
        is Left -> Right(left.value)
        is Right -> Left(right.value)
    }
}

fun <T : Any, U : Any> Either<Either<T, U>, U>.flattenLeft(): Either<T, U> = when (this) {
    is Either.Left -> left.value
    is Either.Right -> this
}

fun <T : Any, U : Any> Either<T, Either<T, U>>.flattenRight(): Either<T, U> = when (this) {
    is Either.Left -> this
    is Either.Right -> right.value
}

class NotLeftException : Exception("not an instance of Either.Left")

class NotRightException : Exception("not an instance of Either.Right")
