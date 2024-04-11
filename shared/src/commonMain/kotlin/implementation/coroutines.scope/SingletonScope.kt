package implementation.coroutines.scope

import api.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    log("Exception $throwable in $coroutineContext")
}

internal enum class SingletonScope(val value: CoroutineScope) {
    IO(CoroutineScope(Dispatchers.IO + SupervisorJob() + exceptionHandler)),
    DEFAULT(CoroutineScope(Dispatchers.Default + SupervisorJob() + exceptionHandler)),
}