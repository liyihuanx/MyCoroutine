package liyihuan.app.android.mycoroutine.exception

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

interface CoroutineExceptionHandler : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<CoroutineExceptionHandler>

    fun handleException(context: CoroutineContext, exception: Throwable)
}

inline fun CoroutineExceptionHandler(crossinline handler: (CoroutineContext, Throwable) -> Unit): CoroutineExceptionHandler {
    val value = object : AbstractCoroutineContextElement(CoroutineExceptionHandler),
        CoroutineExceptionHandler {
        override fun handleException(context: CoroutineContext, exception: Throwable) {
            handler.invoke(context, exception)
        }
    }
    return value
}