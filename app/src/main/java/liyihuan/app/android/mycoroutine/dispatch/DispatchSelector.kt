package liyihuan.app.android.mycoroutine.dispatch

/**
 * @ClassName: DispatchSelector
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/10 22:24
 */
object DispatchSelector {
    val Default by lazy {
        DispatcherContext(DefaultDispatcher)
    }
}