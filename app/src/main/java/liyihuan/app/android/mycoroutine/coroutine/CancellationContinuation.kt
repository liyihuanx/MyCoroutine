package liyihuan.app.android.mycoroutine.coroutine
import liyihuan.app.android.mycoroutine.CancelState
import liyihuan.app.android.mycoroutine.CancellationException
import liyihuan.app.android.mycoroutine.Job
import liyihuan.app.android.mycoroutine.OnCancel
import liyihuan.app.android.mycoroutine.utils.log
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

/**
 * @ClassName: CancellationContinuation
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/11 20:52
 */
class CancellationContinuation<T>(private val continuation: Continuation<T>) : Continuation<T> by continuation {

    private val cancelHandlers = CopyOnWriteArrayList<OnCancel>()
    private val state = AtomicReference<CancelState>(CancelState.InComplete)

    val isCompleted: Boolean
        get() = state.get() is CancelState.Complete<*>

    private val isActive: Boolean
        get() = state.get() == CancelState.InComplete


    // 把所有可取消的suspend函数存起来
    fun invokeOnCancel(onCancel: OnCancel) {
        cancelHandlers += onCancel
    }

    override fun resumeWith(result: Result<T>) {
        state.updateAndGet { oldState ->
            when (oldState) {
                is CancelState.InComplete -> {
                    continuation.resumeWith(result)
                    CancelState.Complete(result.getOrNull(), result.exceptionOrNull())
                }
                is CancelState.Complete<*> -> throw IllegalStateException("Already completed.")
                is CancelState.Cancelled -> {
                    CancellationException("Cancelled.").let {
                        continuation.resumeWith(Result.failure(it))
                        CancelState.Complete(null, it)
                    }
                }
            }
        }
    }

    fun getResult(): Any? {
        installCancelHandler()
        return when (val currentState = state.get()) {
            CancelState.InComplete -> COROUTINE_SUSPENDED
            is CancelState.Complete<*> -> {
                (currentState as CancelState.Complete<T>).let {
                    it.exception?.let { throw it } ?: it.value
                }
            }
            CancelState.Cancelled -> throw CancellationException("Continuation is cancelled.")
        }
    }

    private fun installCancelHandler() {
        if (!isActive) return
        val parent = continuation.context[Job.Key] ?: return
        parent.invokeOnCancel {
            doCancel()
        }
    }

    private fun doCancel() {
        state.updateAndGet { prev ->
            when (prev) {
                CancelState.InComplete -> {
                    CancelState.Cancelled
                }
                is CancelState.Complete<*>,
                CancelState.Cancelled -> {
                    prev
                }
            }
        }

        cancelHandlers.forEach(OnCancel::invoke)
        cancelHandlers.clear()
    }
}

/**
 系统的方法
    public suspend inline fun <T> suspendCancellableCoroutine(
        crossinline block: (CancellableContinuation<T>) -> Unit
    ): T =
        suspendCoroutineUninterceptedOrReturn { uCont ->
            val cancellable = CancellableContinuationImpl(uCont.intercepted(), resumeMode = MODE_CANCELLABLE)
            /*
             * For non-atomic cancellation we setup parent-child relationship immediately
             * in case when `block` blocks the current thread (e.g. Rx2 with trampoline scheduler), but
             * properly supports cancellation.
             */
            cancellable.initCancellability()
            block(cancellable)
            cancellable.getResult()
        }
 */
suspend inline fun <T> suspendCancellableCoroutine(
    crossinline block: (CancellationContinuation<T>) -> Unit
): T = suspendCoroutineUninterceptedOrReturn { continuation: Continuation<T> ->
    val cancellationContinuation = CancellationContinuation(continuation.intercepted())
    block(cancellationContinuation)
    cancellationContinuation.getResult()
}