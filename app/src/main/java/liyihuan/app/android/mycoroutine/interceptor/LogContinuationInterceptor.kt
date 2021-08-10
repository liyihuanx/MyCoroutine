package liyihuan.app.android.mycoroutine.interceptor

import liyihuan.app.android.mycoroutine.utils.log
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @ClassName: MyContinuationInterceptor
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/10 22:44
 */
class MyContinuationInterceptor : ContinuationInterceptor {
    /**
     * A key of this coroutine context element.
     */
    override val key: CoroutineContext.Key<*>
        get() = ContinuationInterceptor

    /**
     * Returns continuation that wraps the original [continuation], thus intercepting all resumptions.
     * This function is invoked by coroutines framework when needed and the resulting continuations are
     * cached internally per each instance of the original [continuation].
     *
     * This function may simply return original [continuation] if it does not want to intercept this particular continuation.
     *
     * When the original [continuation] completes, coroutine framework invokes [releaseInterceptedContinuation]
     * with the resulting continuation if it was intercepted, that is if `interceptContinuation` had previously
     * returned a different continuation instance.
     */
    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
        MyCIContinuation(continuation)

}

class MyCIContinuation<T>(private val coroutineContext: Continuation<T>) : Continuation<T> {
    /**
     * The context of the coroutine that corresponds to this continuation.
     */
    override val context: CoroutineContext = coroutineContext.context

    /**
     * Resumes the execution of the corresponding coroutine passing a successful or failed [result] as the
     * return value of the last suspension point.
     */
    override fun resumeWith(result: Result<T>) {
        log("MyContinuationInterceptor - resumeWithCount$result")
        coroutineContext.resumeWith(result)

    }

}