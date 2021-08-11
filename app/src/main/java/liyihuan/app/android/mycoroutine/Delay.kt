package liyihuan.app.android.mycoroutine

import liyihuan.app.android.mycoroutine.coroutine.suspendCancellableCoroutine
import liyihuan.app.android.mycoroutine.utils.log
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

/**
 * @ClassName: Delay
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/11 21:56
 */

private val executor = Executors.newScheduledThreadPool(1) { runnable ->
    Thread(runnable, "Delay-Scheduler").apply { isDaemon = true }
}


suspend fun delay(time: Long, unit: TimeUnit = TimeUnit.MILLISECONDS) =
    suspendCancellableCoroutine<Unit> {
        val future = executor.schedule({
            it.resume(Unit)
        }, time, unit)
        it.invokeOnCancel {
            future.cancel(true)
        }
    }