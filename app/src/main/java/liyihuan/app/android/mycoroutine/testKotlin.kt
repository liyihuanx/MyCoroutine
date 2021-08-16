package liyihuan.app.android.mycoroutine

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import liyihuan.app.android.mycoroutine.coroutine.suspendCancellableCoroutine
import liyihuan.app.android.mycoroutine.utils.log
import kotlin.concurrent.thread
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.intercepted

/**
 * @author created by liyihuanx
 * @date 2021/8/16
 * @description: 类的描述
 */


fun main() {

    GlobalScope.launch {

    }
}

fun create(block1: suspend () -> Unit) {
    val createCoroutine = block1.createCoroutine(object : Continuation<Unit> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {
            log("返回了结果")
        }
    })
//    createCoroutine.resume(Unit)
}
