package liyihuan.app.android.mycoroutine.scope

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * @ClassName: GlobalScope
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/12 21:05
 */
object GlobalScope : CoroutineScope {
    override val scopeContext: CoroutineContext
        get() = EmptyCoroutineContext
}


// GlobalScope 顶级作用域
// job嵌套，coroutineScope 协同作用域
// supervisorScope 主从作用域