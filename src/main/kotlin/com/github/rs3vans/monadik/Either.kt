package com.github.rs3vans.monadik

sealed class Either<out T : Any, out U : Any> {
    abstract val left: Option<T>
    abstract val right: Option<U>

    val isLeft: Boolean = this is Left
    val isRight: Boolean = this is Right

    operator fun component1() = if (this is Left<T>) left.value else null
    operator fun component2() = if (this is Right<U>) right.value else null

    class Left<out T : Any>(override val left: Option.Some<T>) : Either<T, Nothing>() {
        constructor(left: T) : this(Option.Some(left))

        override val right = Option.None

        override fun toString() = "Left(${left.value})"
        override fun hashCode() = left.hashCode()
        override fun equals(other: Any?) = other is Left<*> && left.equals(other.left)
    }

    class Right<out U : Any>(override val right: Option.Some<U>) : Either<Nothing, U>() {
        constructor(right: U) : this(Option.Some(right))

        override val left = Option.None

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
