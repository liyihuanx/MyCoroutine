package liyihuan.app.android.mycoroutine

import kotlin.coroutines.CoroutineContext

/**
 * @ClassName: Job
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/9 20:44
 */

typealias OnComplete = () -> Unit

typealias CancellationException = java.util.concurrent.CancellationException
typealias OnCancel = () -> Unit

public interface Job : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<Job>

    override val key: CoroutineContext.Key<*> get() = Job

    suspend fun join()

    fun remove(disposable: Disposable)
}