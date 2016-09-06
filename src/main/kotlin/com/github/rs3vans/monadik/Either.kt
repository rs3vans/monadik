package com.github.rs3vans.monadik

sealed class Either<out T : Any, out U : Any> {
    abstract val left: T?
    abstract val right: U?

    operator fun component1() = left
    operator fun component2() = right

    class Left<out T : Any>(override val left: T) : Either<T, Nothing>() {
        override val right = null

        override fun toString() = "Left($left)"
        override fun hashCode() = left.hashCode()
        override fun equals(other: Any?) = other is Left<*> && left.equals(other.left)
    }

    class Right<out U : Any>(override val right: U) : Either<Nothing, U>() {
        override val left = null

        override fun toString() = "Right($right)"
        override fun hashCode() = right.hashCode()
        override fun equals(other: Any?) = other is Right<*> && right.equals(other.right)
    }

    inline fun fold(leftFn: (T) -> Unit, rightFn: (U) -> Unit): Either<T, U> = apply {
        when (this) {
            is Left -> leftFn(left)
            is Right -> rightFn(right)
        }
    }

    inline fun ifLeft(leftFn: (T) -> Unit): Either<T, U> = fold(leftFn, {})

    inline fun ifRight(rightFn: (U) -> Unit): Either<T, U> = fold({}, rightFn)

    fun swap(): Either<U, T> = when (this) {
        is Left -> Right(left)
        is Right -> Left(right)
    }
}

fun <T : Any, U : Any> Either<Either<T, U>, U>.flattenLeft(): Either<T, U> = when (this) {
    is Either.Left -> left
    is Either.Right -> this
}

fun <T : Any, U : Any> Either<T, Either<T, U>>.flattenRight(): Either<T, U> = when (this) {
    is Either.Left -> this
    is Either.Right -> right
}
