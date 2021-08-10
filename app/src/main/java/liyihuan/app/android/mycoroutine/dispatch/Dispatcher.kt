package liyihuan.app.android.mycoroutine.dispatch

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor

/**
 * @ClassName: Dispatcher
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/10 22:15
 */

interface Dispatcher {
    fun dispatch(block: ()->Unit)
}

// 调度器本质就是拦截器
// 在拦截器中，添加一个调度器Dispatch，里面包含一个线程池去执行 --> 跟okHttp中添加各种拦截器差不多一个意思
open class DispatcherContext(private val dispatcher: Dispatcher) : AbstractCoroutineContextElement(
    // ContinuationInterceptor.Key 和 ContinuationInterceptor 一个样
    ContinuationInterceptor.Key
), ContinuationInterceptor {
    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T>
            = DispatchedContinuation(continuation, dispatcher)


}

private class DispatchedContinuation<T>(val delegate: Continuation<T>, val dispatcher: Dispatcher) : Continuation<T>{
    override val context = delegate.context

    override fun resumeWith(result: Result<T>) {
        dispatcher.dispatch {
            delegate.resumeWith(result)
        }
    }
}