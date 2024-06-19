package implementation.domain.models

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>

    data class Failure(val throwable: Throwable? = null) : Resource<Nothing>
}

fun <T> T.toSuccessResource() = Resource.Success(this)

inline fun <T> Resource<T>.onSuccess(block: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        block(data)
    }

    return this
}

inline fun <T, R> Resource<T>.flatMap(block: (T) -> Resource<R>): Resource<R> {
    return when (this) {
        is Resource.Failure -> Resource.Failure(throwable)
        is Resource.Success -> block(data)
    }
}

inline fun <T, R> Resource<T>.map(block: (T) -> R): Resource<R> = flatMap {
    block(it).toSuccessResource()
}