package liyihuan.app.android.mycoroutine.scope

import liyihuan.app.android.mycoroutine.Job
import liyihuan.app.android.mycoroutine.coroutine.AbsCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * @ClassName: CoroutineScope
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/12 21:11
 */

interface CoroutineScope {
    val scopeContext: CoroutineContext
}

internal class ContextScope(context: CoroutineContext): CoroutineScope {
    override val scopeContext: CoroutineContext = context
}

suspend fun <R> coroutineScope(block: suspend CoroutineScope.() -> R): R =
    suspendCoroutine {
            continuation ->
        val coroutine = ScopeCoroutine(continuation.context, continuation)
        block.startCoroutine(coroutine,coroutine)
    }

internal open class ScopeCoroutine<T>(
    context: CoroutineContext,
    private val continuation: Continuation<T>
) : AbsCoroutine<T>(context) {
    override fun resumeWith(result: Result<T>) {
        super.resumeWith(result)
        continuation.resumeWith(result)
    }
}
