package liyihuan.app.android.mycoroutine.dispatch

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * @ClassName: DefaultDispatcher
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/8/10 22:23
 */
object DefaultDispatcher : Dispatcher {

    private val threadGroup = ThreadGroup("DefaultDispatcher")
    private val threadIndex = AtomicInteger(0)

    private val executor = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors()){
            runnable ->
        Thread(threadGroup, runnable, "${threadGroup.name}-worker-${threadIndex.getAndIncrement()}")
            .apply {
                isDaemon = true
            }
    }


    override fun dispatch(block: () -> Unit) {
        executor.submit(block)
    }

}