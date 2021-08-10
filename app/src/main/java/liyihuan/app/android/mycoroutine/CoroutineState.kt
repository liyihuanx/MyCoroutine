package liyihuan.app.android.mycoroutine

/**
 * @ClassName: CoroutineStatus
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/9 21:02
 */

sealed class CoroutineState {

    class InComplete : CoroutineState()
    class Cancelling : CoroutineState()
    class Complete<T>(val value: T? = null, val exception: Throwable? = null) : CoroutineState()


    private var disposableList: DisposableList = DisposableList.Nil

    fun from(state: CoroutineState): CoroutineState {
        this.disposableList = state.disposableList
        return this
    }

    fun with(disposable: Disposable): CoroutineState {
        this.disposableList = DisposableList.Cons(disposable, this.disposableList)
        return this
    }

    fun without(disposable: Disposable): CoroutineState {
        this.disposableList = this.disposableList.remove(disposable)
        return this
    }

    fun <T> notifyCompletion(result: Result<T>) {
        this.disposableList.loopOn<CompletionHandlerDisposable<T>> {
            it.onComplete(result)
        }
    }

    fun notifyCancellation() {
        disposableList.loopOn<CancellationHandlerDisposable> {
            it.onCancel()
        }
    }


    fun clear() {
        this.disposableList = DisposableList.Nil
    }

    override fun toString(): String {
        return "CoroutineState.${this.javaClass.simpleName}"
    }


}