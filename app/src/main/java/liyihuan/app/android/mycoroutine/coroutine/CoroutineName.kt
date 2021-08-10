package liyihuan.app.android.mycoroutine.coroutine

import kotlin.coroutines.CoroutineContext

class CoroutineName(private val name: String): CoroutineContext.Element {
    companion object Key: CoroutineContext.Key<CoroutineName>

    override val key = Key

    override fun toString(): String {
        return name
    }
}