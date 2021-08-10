package liyihuan.app.android.mycoroutine

import com.google.gson.Gson
import liyihuan.app.android.mycoroutine.coroutine.CoroutineName
import liyihuan.app.android.mycoroutine.coroutine.StandardCoroutine
import liyihuan.app.android.mycoroutine.dispatch.DispatchSelector
import liyihuan.app.android.mycoroutine.interceptor.MyContinuationInterceptor
import liyihuan.app.android.mycoroutine.utils.log
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/**
 * @ClassName: Build
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/9 20:38
 */

fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> Unit
): Job {
    // 源码
//    val newContext = newCoroutineContext(context)
//    val coroutine = if (start.isLazy)
//        LazyStandaloneCoroutine(newContext, block) else
//        StandaloneCoroutine(newContext, active = true)
//    coroutine.start(start, coroutine, block)

    val newContext = newCoroutineContext(context)
    val coroutine = StandardCoroutine(newContext)
    block.startCoroutine(coroutine)
    return coroutine
}

// CoroutineContext == 数据结构
private var coroutineIndex = AtomicInteger(0)
fun newCoroutineContext(context: CoroutineContext): CoroutineContext {
    /**
     * coroutineContext{"left":{"key":{},"name":"@coroutine#0"},"element":{"dispatcher":{},"key":{}}}
     * {
            "left":{
                "key":Object{...},
                "name":"@coroutine#0"
            },
            "element":{
                "dispatcher":Object{...},
                "key":Object{...}
            }
        }
     */
    // "+" 是重载运算符
    val combined =
        context + CoroutineName("@coroutine#${coroutineIndex.getAndIncrement()}")

    return if (combined !== DispatchSelector.Default && combined[ContinuationInterceptor] == null)
        combined + DispatchSelector.Default  else combined

}