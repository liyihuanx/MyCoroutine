package liyihuan.app.android.mycoroutine

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
    val jon = launch {
        log(1)
        val result = test()
        log(result)
        log(2)

    }

    jon.join()
}

suspend fun test() = suspendCoroutine<String> { it ->

    thread(isDaemon = true) {
        Thread.sleep(1000)
        it.resume("Hello-World")
    }


}