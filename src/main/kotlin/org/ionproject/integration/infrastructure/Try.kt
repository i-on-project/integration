package org.ionproject.integration.infrastructure

class CompositeException(val exceptions: List<Exception>) : Exception()

private fun merge(e1: Exception, e2: Exception) =
    when (e1) {
        is CompositeException -> when (e2) {
            is CompositeException -> CompositeException(e1.exceptions + e2.exceptions)
            else -> CompositeException(e1.exceptions + e2)
        }
        else -> when (e2) {
            is CompositeException -> CompositeException(e2.exceptions + e1)
            else -> CompositeException(listOf(e1, e2))
        }
    }

sealed class Try<out T> {
    abstract fun <R> map(f: (T) -> R): Try<R>
    abstract fun mapError(f: (Exception) -> Exception): Try<T>
    abstract fun <R> flatMap(f: (T) -> Try<R>): Try<R>
    abstract fun <R> match(success: (T) -> R, error: (Exception) -> R): R

    class Value<out T>(val value: T) : Try<T>() {

        override fun <R> map(f: (T) -> R): Try<R> = of { f(value) }

        override fun mapError(f: (Exception) -> Exception): Try<T> = this

        override fun <R> flatMap(f: (T) -> Try<R>): Try<R> = try {
            f(value)
        } catch (e: Exception) {
            Error(e)
        }

        override fun <R> match(success: (T) -> R, error: (Exception) -> R) = success(value)
    }

    class Error(val e: Exception) : Try<Nothing>() {

        override fun <R> map(f: (Nothing) -> R): Try<R> = this
        override fun mapError(f: (Exception) -> Exception) = Error(merge(e, f(e)))
        override fun <R> flatMap(f: (Nothing) -> Try<R>): Try<R> = this
        override fun <R> match(success: (Nothing) -> R, error: (Exception) -> R) = error(e)
    }

    companion object {
        fun <T> of(f: () -> T): Try<T> = try {
            Value(f())
        } catch (e: Exception) {
            Error(e)
        }

        fun <T> ofValue(value: T) = Value(value)
        fun <T> ofError(e: Exception) = Error(e)

        fun <T1, T2, R> map(a: Try<T1>, b: Try<T2>, f: (T1, T2) -> R): Try<R> =
            when (a) {
                is Value -> when (b) {
                    is Value -> of { f(a.value, b.value) }
                    is Error -> Error(b.e)
                }
                is Error -> when (b) {
                    is Value -> Error(a.e)
                    is Error -> Error(merge(a.e, b.e))
                }
            }

        fun <T, R> map(vararg values: Try<T>, f: (List<T>) -> R): Try<R> {
            data class Holder(val list: MutableList<T> = mutableListOf(), var error: Error? = null)

            val data = values.fold(Holder()) { holder, item ->
                holder.apply {
                    when (item) {
                        is Value -> list += item.value
                        is Error -> {
                            val exception = error?.let { merge(it.e, item.e) } ?: item.e
                            error = Error(exception)
                        }
                    }
                }
            }
            return data.error ?: of { f(data.list) }
        }
    }
}

fun <T> Try<T>.orElse(ifError: T) = this.match(
    { it },
    { ifError }
)

fun <T> Try<T>.orElse(f: (Exception) -> T) = this.match(
    { it },
    { f(it) }
)

fun <T> Try<T>.orThrow() = this.match(
    { it },
    { throw it }
)
