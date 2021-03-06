package liyihuan.app.android.mycoroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import liyihuan.app.android.mycoroutine.utils.log
import java.lang.Exception
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

suspend fun main() {

    val job = GlobalScope.launch {
        log("发起网络请求")
        val test1 = Http()
        log("网络请求返回结果$test1")
    }
    kotlinx.coroutines.delay(100)
    log("关闭了页面，取消网络求情")
    job.cancel()
    job.join()
    kotlinx.coroutines.delay(2000)


//    val job1 = GlobalScope.launch {
//        log(1)
//        kotlinx.coroutines.delay(2000)
//        val job2 = GlobalScope.launch {
//            throw ArithmeticException("11")
//        }
//
//        val job3 = GlobalScope.launch {
//            log(2)
//        }
//
//        log(3)
//    }
//
//    job1.join()
//    kotlinx.coroutines.delay(2000)


}

suspend fun Http() =
    suspendCancellableCoroutine<String> { it ->
        thread(isDaemon = true) {
            Thread.sleep(200)
            it.resume("Hello-World")
        }
    }
