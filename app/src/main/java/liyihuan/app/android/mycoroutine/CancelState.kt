package liyihuan.app.android.mycoroutine

/**
 * @ClassName: CancelState
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/11 20:54
 */
sealed class CancelState {
    override fun toString(): String {
        return "CancelState.${javaClass.simpleName}"
    }

    object InComplete : CancelState()
    class Complete<T>(val value: T? = null, val exception: Throwable? = null) : CancelState()
    object Cancelled : CancelState()
}