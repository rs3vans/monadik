package com.github.rs3vans.monadik

inline fun <T : Any, U : Any> T?.flatMap(transformer: (T) -> U?): U? = this?.let(transformer)

inline fun <T : Any, U : Any> T?.map(transformer: (T) -> U): U? = flatMap { transformer(it) }

inline fun <T : Any> T?.filter(pred: (T) -> Boolean): T? = flatMap { if(pred(it)) this else null }

inline fun <T : Any> T?.fold(left: (T) -> Unit, right: () -> Unit): T? =
        apply { if (this != null) left(this) else right() }

inline fun <T : Any> T?.ifPresent(left: (T) -> Unit): T? = fold(left, {})

inline fun <T : Any> T?.ifAbsent(right: () -> Unit): T? = fold({}, right)