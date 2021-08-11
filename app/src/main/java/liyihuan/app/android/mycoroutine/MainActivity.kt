package liyihuan.app.android.mycoroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import liyihuan.app.android.mycoroutine.utils.log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

suspend fun main() {

    val job = GlobalScope.launch {
        log("发起网络请求")
        kotlinx.coroutines.delay(200)
        log("网络请求返回结果")
    }
    kotlinx.coroutines.delay(100)
    log("关闭了页面，取消网络求情")
    job.cancel()
    job.join()
    kotlinx.coroutines.delay(200)

}