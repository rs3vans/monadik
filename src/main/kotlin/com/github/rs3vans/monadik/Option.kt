package com.github.rs3vans.monadik

/**
 * The Option monad, which represents a possibly present value, or the absence thereof.
 *
 * While it's true that Kotlin doesn't _need_ the Option monad due to it's compile-time null-safety,
 * there are some benefits to having the runtime concept of an Option type. For more discussion on
 * this topic, please view the [MonadiK Wiki](https://github.com/rs3vans/monadik/wiki).
 *
 * An instance of [Option] is either [Some] (wrapping a non-null value) or [None].
 *
 * To construct an instance of [Option], either call the [Some] constructor (with a non-null reference),
 * or call [Option.invoke] with any nullable reference.
 *
 * @param T the type this monad wraps
 *
 * @author Ryan Evans
 */
sealed class Option<out T : Any> {

    /**
     * The wrapped value when `this` is [Some], otherwise throws a [NullPointerException].
     *
     * @throws NullPointerException when `this` is [None]
     */
    abstract val value: T

    /**
     * Returns `true` when `this` is [Some], otherwise `false`.
     */
    val present: Boolean = this is Some

    /**
     * Returns `true` when `this` is [None], otherwise `false`.
     */
    val absent: Boolean = this is None

    /**
     * Overloaded not (\!) operator.
     *
     * @return `true` when `this` is [None], otherwise `false`
     */
    operator fun not() = absent

    /**
     * Overloaded contains (in) operator.
     *
     * @return `true` when `this` is [Some] and the contained [value] is equal to [other], otherwise `false`
     */
    operator fun contains(other: Any?): Boolean = present && value == other

    /**
     * The concrete subtype of [Option] which represents a present value.
     *
     * @param T the type of the wrapped value
     * @property value the wrapped value itself
     * @constructor creates a new instance of [Some] wrapping the given [value]
     */
    class Some<out T : Any>(override val value: T) : Option<T>() {
        override fun toString() = "Some($value)"
        override fun hashCode() = value.hashCode()
        override fun equals(other: Any?) = other is Option<*> && value.equals(other.value)
    }

    /**
     * The concrete subtype of [Option] which represents the absence of a value.
     *
     * Note: [None] is an object singleton and therefore cannot be instantiated.
     */
    object None : Option<Nothing>() {
        override val value: Nothing
            get() = throw NullPointerException()

        override fun toString() = "None"
    }

    /**
     * Produces a new [Option] by applying the given [transformer] to [value] if `this` is an instance of [Some],
     * or returning [None] otherwise.
     *
     * @return a new, transformed [Some], or [None]
     */
    inline fun <U : Any> flatMap(transformer: (T) -> Option<U>): Option<U> = when (this) {
        is Some -> transformer(value)
        is None -> this
    }

    /**
     * Produces a new [Option] by applying the given [transformer] to [value] (if `this` is an instance of [Some]) and
     * then wrapping that in an [Option], or returning [None] otherwise.
     *
     * @return a new, transformed [Some] or [None]
     */
    inline fun <U : Any> map(transformer: (T) -> U): Option<U> = flatMap { Option(transformer(it)) }

    /**
     * Filters this [Option] by returning `this` if it is an instance of [Some] _and_ it satisfies the given
     * [predicate], or [None] otherwise.
     *
     * @return `this` or [None]
     */
    inline fun filter(predicate: (T) -> Boolean): Option<T> = flatMap { if (predicate(it)) this else None }

    /**
     * Invokes the given [consumer][left] if `this` is an instance of [Some], otherwise invokes the given
     * [runnable][right].
     *
     * @return `this`
     */
    inline fun fold(left: (T) -> Unit, right: () -> Unit): Option<T> = apply {
        when (this) {
            is Some -> left(value)
            is None -> right()
        }
    }

    /**
     * Invokes the given [consumer][left] if `this` is an instance of [Some], otherwise does nothing.
     *
     * @param left consumer invoked if `this` is an instance of [Some]
     * @return `this`
     */
    inline fun ifPresent(left: (T) -> Unit): Option<T> = fold(left, {})

    /**
     * Invokes the given [runnable][right] if `this` is [None], otherwise does nothing.
     *
     * @param right runnable invoked if `this` is [None]
     * @return `this`
     */
    inline fun ifAbsent(right: () -> Unit): Option<T> = fold({}, right)

    /**
     * Converts this [Option] into a [left][Either.Left] (of type [T]) if `this` is an instance of [Some],
     * otherwise returns a [right][Either.Right] using the given [value][right] (of type [U]).
     *
     * @param right right value used if `this` is [None]
     * @return either [Either.Left] or [Either.Right]
     */
    fun <U : Any> toLeft(right: U): Either<T, U> = when (this) {
        is Some -> Either.Left(value)
        is None -> Either.Right(right)
    }

    /**
     * Converts this [Option] into a [right][Either.Right] (of type [T]) if `this` is an instance of [Some],
     * otherwise returns a [left][Either.Left] using the given [value][left] (of type [U]).
     *
     * @param left left value used if `this` is [None]
     * @return either [Either.Right] or [Either.Left]
     */
    fun <U : Any> toRight(left: U): Either<U, T> = when (this) {
        is Some -> Either.Right(value)
        is None -> Either.Left(left)
    }

    /**
     * Converts this [Option] into a [successful][Try.Success] (of type [T]) if `this` is an instance of [Some],
     * otherwise returns a [failure][Try.Failure] with the exception obtained by invoking [exception].
     *
     * @param exception exception supplier invoked if `this` is [None]
     * @return either [Try.Success] or [Try.Failure]
     */
    fun toTry(exception: () -> Exception = { NullPointerException() }): Try<T> = when (this) {
        is Some -> Try.Success(value)
        is None -> Try.Failure(exception())
    }

    companion object {

        /**
         * Generates an [Option], returning a new instance of [Some] if [value] is not null, otherwise [None].
         *
         * @param value the (possibly null) value to wrap
         * @return [Some] or [None], depending on [value]
         */
        operator fun <T : Any> invoke(value: T?): Option<T> = if (value != null) {
            Some(value)
        } else {
            None
        }
    }
}

/**
 * Flattens an [Option] which wraps another [Option].
 *
 * @return the [Option] which was previously wrapped in another [Option]
 */
fun <T : Any> Option<Option<T>>.flatten(): Option<T> = when (this) {
    is Option.Some -> value
    is Option.None -> this
}

/**
 * Terminates this [Option] by returning its [value][Option.value] when it is an instance of [Option.Some],
 * otherwise returning the value produced by invoking the given [supplier][other].
 *
 * @param T the type wrapped by this [Option], which is returned
 * @param other the supplier invoked if this is [Option.None]
 * @returns a value of type [T]
 */
inline fun <T : Any> Option<T>.orElseGet(other: () -> T): T = when (this) {
    is Option.Some -> value
    is Option.None -> other()
}

/**
 * Terminates this [Option] by returning its [value][Option.value] when it is an instance of [Option.Some],
 * otherwise returning the given [value][other].
 *
 * @param T the type wrapped by this [Option], which is returned
 * @param other the value returned if this is [Option.None]
 * @returns a value of type [T]
 */
fun <T : Any> Option<T>.orElse(other: T): T = orElseGet { other }

/**
 * Terminates this [Option] by returning its [value][Option.value] when it is an instance of [Option.Some],
 * otherwise returning `null`.
 *
 * @param T the type wrapped by this [Option], which is returned
 * @returns a value of type [T], or `null`
 */
fun <T : Any> Option<T>.orNull(): T? = when (this) {
    is Option.Some -> value
    is Option.None -> null
}

/**
 * Terminates this [Option] by returning its [value][Option.value] when it is an instance of [Option.Some],
 * otherwise throwing the exception produced by invoking the given [supplier][exception].
 *
 * @param T the type wrapped by this [Option], which is returned
 * @param exception the supplier invoked if this is [Option.None]
 * @returns a value of type [T]
 * @throws Exception given by the [exception] supplier if this is [Option.None]
 */
inline fun <T : Any> Option<T>.orElseThrow(exception: () -> Exception): T = orElseGet { throw exception() }

/**
 * Wraps the [value][Option.value] wrapped in this [Option] in a [List], or returns an [empty list][emptyList] if
 * this is [Option.None].
 *
 * @return an instance of [List]
 */
fun <T : Any> Option<T>.toList(): List<T> = map { listOf(it) }.orElseGet { emptyList() }

/**
 * Wraps the [value][Option.value] wrapped in this [Option] in a [Set], or returns an [empty set][emptySet] if
 * this is [Option.None].
 *
 * @return an instance of [Set]
 */
fun <T : Any> Option<T>.toSet(): Set<T> = map { setOf(it) }.orElseGet { emptySet() }

/**
 * Wraps the [value][Option.value] wrapped in this [Option] in a [Sequence], or returns an
 * [empty sequence][emptySequence] if this is [Option.None].
 *
 * @return an instance of [Sequence]
 */
fun <T : Any> Option<T>.toSequence(): Sequence<T> = map { sequenceOf(it) }.orElseGet { emptySequence() }