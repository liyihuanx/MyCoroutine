package liyihuan.app.android.mycoroutine

import liyihuan.app.android.mycoroutine.coroutine.CoroutineName
import liyihuan.app.android.mycoroutine.coroutine.StandardCoroutine
import liyihuan.app.android.mycoroutine.dispatch.DispatchSelector
import liyihuan.app.android.mycoroutine.scope.CoroutineScope
import liyihuan.app.android.mycoroutine.scope.GlobalScope
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

fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit // CoroutineScope里的方法只能在这个闭包中使用){}
): Job {
    // 源码
//    val newContext = newCoroutineContext(context)
//    val coroutine = if (start.isLazy)
//        LazyStandaloneCoroutine(newContext, block) else
//        StandaloneCoroutine(newContext, active = true)
//    coroutine.start(start, coroutine, block)

    val newContext = newCoroutineContext(context)
    val coroutine = StandardCoroutine(newContext)
    block.startCoroutine(coroutine,coroutine)
    return coroutine
}

// CoroutineContext == 数据结构
private var coroutineIndex = AtomicInteger(0)
fun CoroutineScope.newCoroutineContext(context: CoroutineContext): CoroutineContext {
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
        context + scopeContext + CoroutineName("@coroutine#${coroutineIndex.getAndIncrement()}")

    return if (combined !== DispatchSelector.Default && combined[ContinuationInterceptor] == null)
        combined + DispatchSelector.Default  else combined

}