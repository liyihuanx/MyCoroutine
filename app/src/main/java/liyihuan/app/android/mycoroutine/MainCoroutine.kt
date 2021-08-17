package liyihuan.app.android.mycoroutine

import liyihuan.app.android.mycoroutine.coroutine.suspendCancellableCoroutine
import liyihuan.app.android.mycoroutine.scope.GlobalScope
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
     val job = GlobalScope.launch {
        log("发起网络请求")
        val test1 = test()
        log("网络请求返回结果$test1")
    }
    delay(200)
    log("关闭了页面，取消网络求情")
    job.cancel()
    job.join()
    delay(200)


}

suspend fun test() = suspendCancellableCoroutine<String> { it ->
    thread(isDaemon = true) {
        Thread.sleep(300)
        it.resume("Hello-World")
    }
}