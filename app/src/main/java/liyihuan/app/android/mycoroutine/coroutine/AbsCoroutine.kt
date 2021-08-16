package liyihuan.app.android.mycoroutine.coroutine

import liyihuan.app.android.mycoroutine.*
import liyihuan.app.android.mycoroutine.exception.CoroutineExceptionHandler
import liyihuan.app.android.mycoroutine.scope.CoroutineScope
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.*

/**
 * @ClassName: AbsCoroutine
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/9 20:43
 */
abstract class AbsCoroutine<T>(newContext: CoroutineContext) : Job, Continuation<T>,
    CoroutineScope {

    // 原子操作,当前协程的状态
    private val state = AtomicReference<CoroutineState>()

    override val context: CoroutineContext = newContext + this

    override val scopeContext: CoroutineContext
        get() = context

    override val isActive: Boolean
        get() = state.get() is CoroutineState.InComplete

    override val isCompleted: Boolean
        get() = state.get() is CoroutineState.Complete<*>

    // 传进来的context会包含父协程,在newCoroutineContext时一起添加进来的
    protected val parentJob = newContext[Job]

    private var parentCancelDisposable: Disposable? = null

    init {
        state.set(CoroutineState.InComplete())
        parentCancelDisposable = parentJob?.invokeOnCancel {
            cancel()
        }
    }

    override suspend fun join() {
        when (state.get()) {
            is CoroutineState.Cancelling,
            is CoroutineState.InComplete -> {
                // 未完成 挂起等待
                return joinSuspend()
            }
            is CoroutineState.Complete<*> -> {
                // coroutineContext --> Returns the context of the current coroutine.
                // 有无当前协程 有判断存活状态，没有直接return
                val currentCallingJobState = coroutineContext[Job]?.isActive ?: return
                // isActive == InComplete
                if (!currentCallingJobState) {
                    throw CancellationException("Coroutine is cancelled.")
                }
                return
            }
        }
    }


    /**
     * 挂起当前job，等待当前挂起函数resume，否则joinSuspend不会恢复
     */
    private suspend fun joinSuspend() = suspendCancellableCoroutine<Unit> { continuation ->
        val disposable = doOnCompleted {
            continuation.resume(Unit)
        }
        // 把当前job的onCancel方法存起来
        continuation.invokeOnCancel {
            disposable.dispose()
        }
    }

    private fun doOnCompleted(block: (Result<T>) -> Unit): Disposable {
        // 把 disposable 和 job 建立联系，并把joinSuspend 挂起函数的开始方法一起做保存
        val disposable = CompletionHandlerDisposable(this, block)
        // 判断当前job状态
        val newState = state.updateAndGet { oldState ->
            // 拿到调用resumeWith方法时的状态
            when (oldState) {
                is CoroutineState.InComplete -> {
                    // 未完成
                    // 1.CoroutineState.InComplete() --> 新建一个CoroutineState(),默认DisposableList() == null，且作为新State
                    // 2.from() --> 复制，把旧的State复制一份存放在新State中
                    // 3.with() --> 向新的State 添加 disposable 并以Bean类 Cons() { head(Disposable)对象 和 tail(disposableList)} 存放
                    CoroutineState.InComplete().from(oldState).with(disposable)
                }
                is CoroutineState.Cancelling -> {
                    CoroutineState.Cancelling().from(oldState).with(disposable)
                }
                is CoroutineState.Complete<*> -> {
                    oldState
                }
            }

        }
//        newState as? CoroutineState.Complete<T>  --> CoroutineState.Complete<T>?
//        newState as CoroutineState.Complete<T> --> CoroutineState.Complete<T>

        // 为了给 async 用的，返回一个结果
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
        state.updateAndGet { oldState ->
            when (oldState) {
                is CoroutineState.InComplete -> {

                    CoroutineState.InComplete().from(oldState).without(disposable)
                }
                is CoroutineState.Complete<*> -> oldState
                is CoroutineState.Cancelling -> {
                    CoroutineState.Cancelling().from(oldState).without(disposable)
                }
            }
        }
    }


    override fun cancel() {
        val newState = state.updateAndGet { oldState ->
            when (oldState) {
                is CoroutineState.InComplete -> {
                    CoroutineState.Cancelling().from(oldState)
                }
                is CoroutineState.Complete<*>,
                is CoroutineState.Cancelling -> {
                    oldState
                }
            }
        }

        // newState 里面存有当前job的disposableList
        if (newState is CoroutineState.Cancelling) {
            newState.notifyCancellation()
        }

        parentCancelDisposable?.dispose()
    }


    override fun invokeOnCancel(onCancel: OnCancel): Disposable {
        val disposable = CancellationHandlerDisposable(this, onCancel)
        val newState = state.updateAndGet { oldState ->
            when (oldState) {
                is CoroutineState.InComplete -> {
                    CoroutineState.InComplete().from(oldState).with(disposable)
                }
                is CoroutineState.Cancelling,
                is CoroutineState.Complete<*> -> {
                    oldState
                }
            }
        }

        (newState as? CoroutineState.Cancelling)?.let {
            onCancel()
        }
        return disposable
    }

    override fun invokeOnCompletion(onComplete: OnComplete): Disposable {
        return doOnCompleted {
            onComplete()
        }
    }

    override fun resumeWith(result: Result<T>) {
        // 根据旧状态扭转成新状态
        val newState = state.updateAndGet { oldState ->
            // 拿到调用resumeWith方法时的状态
            when (oldState) {
                is CoroutineState.Cancelling,
                is CoroutineState.InComplete -> {
                    CoroutineState.Complete(result.getOrNull(), result.exceptionOrNull())
                        .from(oldState)
                }

                is CoroutineState.Complete<*> -> {
                    throw Exception("resumeWith 已经 Complete 过了")

                }
            }
        }

        // 扭转的新状态 --> 已完成
        // 判断是否有异常。
        (newState as CoroutineState.Complete<T>).exception?.let {
            tryHandleException(it)
        }
        // 更新
        newState.notifyCompletion(result)
        newState.clear()

        parentCancelDisposable?.dispose()

    }

    /**
     * 有异常,想给父job处理，不处理的话再由自己处理
     */
    private fun tryHandleException(exception: Throwable): Boolean {
        return when (exception) {
            is CancellationException -> false
            else -> {
                // (xxx)? return (true or false).takeIf{ } --> 如果为false就handleJobExp
                (parentJob as? AbsCoroutine<*>)?.handleChildException(exception)?.takeIf { it }
                    ?: handleJobException(exception)
            }
        }
    }

    protected open fun handleChildException(exception: Throwable): Boolean {
        cancel()
        return tryHandleException(exception)
    }


    // 返回true表示已经处理过异常了
    protected open fun handleJobException(e: Throwable): Boolean {
        val coroutineExceptionHandler = context[CoroutineExceptionHandler.Key]
        //
        coroutineExceptionHandler?.handleException(context, e)
            ?: Thread.currentThread().let {
                it.uncaughtExceptionHandler.uncaughtException(it, e)
            }
        return true
    }


    override fun toString(): String {
        return "${context[CoroutineName]?.name}"
    }
}

