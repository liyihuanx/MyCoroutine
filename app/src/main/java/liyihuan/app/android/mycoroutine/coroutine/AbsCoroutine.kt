package liyihuan.app.android.mycoroutine.coroutine

import liyihuan.app.android.mycoroutine.CompletionHandlerDisposable
import liyihuan.app.android.mycoroutine.CoroutineState
import liyihuan.app.android.mycoroutine.Disposable
import liyihuan.app.android.mycoroutine.Job
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @ClassName: AbsCoroutine
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/9 20:43
 */
abstract class AbsCoroutine<T>(newContext: CoroutineContext) : Job, Continuation<T> {

    // 原子操作,当前协程的状态
    private val state = AtomicReference<CoroutineState>()

    override val context: CoroutineContext = newContext

    override val isActive: Boolean
        get() = state.get() is CoroutineState.InComplete

    override val isCompleted: Boolean
        get() = state.get() is CoroutineState.Complete<*>

    init {
        state.set(CoroutineState.InComplete())
    }

    override suspend fun join() {
        when (state.get()) {
            is CoroutineState.InComplete -> {
                // 未完成 挂起等待
                return joinSuspend()
            }
            is CoroutineState.Cancelling -> {

            }
            is CoroutineState.Complete<*> -> {
                return
            }
        }
    }


    private suspend fun joinSuspend() = suspendCoroutine<Unit> { continuation ->
        // job 完成后 执行doOnCompleted的闭包？
        doOnCompleted {
            continuation.resume(Unit)
        }
    }

    private fun doOnCompleted(block: (Result<T>) -> Unit): Disposable {
        // 把 disposable 和 job 建立联系
        val disposable = CompletionHandlerDisposable(this, block)
        // 判断当前job状态
        val newState = state.updateAndGet { oldState ->
            // 拿到调用resumeWith方法时的状态
            when (oldState) {
                is CoroutineState.InComplete -> {
                    // 未完成 -->
                    CoroutineState.InComplete().from(oldState).with(disposable)
                }
                is CoroutineState.Cancelling -> {
                    CoroutineState.Cancelling()
                }
                is CoroutineState.Complete<*> -> {
                    oldState
                }
            }

        }
//        newState as? CoroutineState.Complete<T>  --> CoroutineState.Complete<T>?
//        newState as CoroutineState.Complete<T> --> CoroutineState.Complete<T>

        (newState as? CoroutineState.Complete<T>)?.let {
            block(
                when {
                    it.value != null -> Result.success(it.value)
                    it.exception != null -> Result.failure(it.exception)
                    else -> throw Exception("Won't happen!")
                }
            )
        }
        return disposable
    }

    override fun remove(disposable: Disposable) {

    }

    override fun resumeWith(result: Result<T>) {
        // 根据旧状态扭转成新状态
        val newState = state.updateAndGet { oldState ->
            // 拿到调用resumeWith方法时的状态
            when (oldState) {
                is CoroutineState.InComplete -> {
                    CoroutineState.Complete(result.getOrNull(), result.exceptionOrNull())
                }
                is CoroutineState.Cancelling -> {
                    CoroutineState.Cancelling()
                }
                is CoroutineState.Complete<*> -> {
                    throw Exception("resumeWith 已经 Complete 过了")

                }
            }
        }

        // 扭转的新状态 --> 已完成
        // 判断是否有异常。
        (newState as CoroutineState.Complete<T>).exception?.let {

        }
        // 更新
        newState.notifyCompletion(result)
        newState.clear()

    }
}

