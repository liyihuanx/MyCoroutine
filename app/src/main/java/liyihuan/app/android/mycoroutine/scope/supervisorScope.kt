package liyihuan.app.android.mycoroutine.scope

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * @ClassName: supervisorScope
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/12 22:18
 */

private class SupervisorCoroutine<T>(context: CoroutineContext, continuation: Continuation<T>) :
    ScopeCoroutine<T>(context, continuation) {

    // 子协程给的异常不处理，让子协程自己处理
    override fun handleChildException(e: Throwable): Boolean {
        return false
    }

}

suspend fun <R> supervisorScope(block: suspend CoroutineScope.() -> R): R =
    suspendCoroutine {
            continuation ->
        val coroutine = SupervisorCoroutine(continuation.context, continuation)
        block.startCoroutine(coroutine, coroutine)
    }