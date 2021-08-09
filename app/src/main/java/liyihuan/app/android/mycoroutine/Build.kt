package liyihuan.app.android.mycoroutine

import liyihuan.app.android.mycoroutine.coroutine.StandardCoroutine
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


fun newCoroutineContext(context: CoroutineContext): CoroutineContext {
    return context
}