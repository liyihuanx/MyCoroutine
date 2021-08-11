package liyihuan.app.android.mycoroutine

import liyihuan.app.android.mycoroutine.coroutine.suspendCancellableCoroutine
import liyihuan.app.android.mycoroutine.utils.log
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @ClassName: MainCoroutine
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/9 20:41
 */

suspend fun main() {
    log("main")

    val jon = launch {
        log(1)
        val result = test()
        log(result)
//        delay(2000)
        log(2)
    }

    delay(100)
    jon.cancel()
    jon.join()
    log(3)
    delay(200)
    log(4)

}

suspend fun test() = suspendCancellableCoroutine<String> { it ->
    thread(isDaemon = true) {
        Thread.sleep(200)
        it.resume("Hello-World")
    }
}