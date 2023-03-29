package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.*
import kotlin.random.Random


fun main() {

    val coroutineLifecycle = CoroutineLifecycle()
//    coroutineLifecycle.test()
//    coroutineLifecycle.test2()
//    coroutineLifecycle.test3()
    coroutineLifecycle.test4()

}

/**
 * kotlin 协程的 Job (句柄)
 *
 * 如何理解 Job 是协程的句柄？
 * Job 与协程的关系有点像 "遥控器和空调的关系"：
 *      遥控器可以监测空调的运行状态；Job 也可以监测协程的运行状态；
 *      遥控器可以操控空调的运行状态；Job 也可以操控协程的运行状态；
 * 所以，从某种程度上来说，遥控器是空调对外暴露的一个"句柄"。
 *
 * 当我们使用 launch 和 async 创建一个协程的时候，同时也会创建一个对应的 Job 对象。
 * Job 是我们理解 协程声明周期、结构化并发 的关健知识点。通过 Job 暴露的 API，我们还可以让不同的协程之间
 * 相互配合，从而实现更加复杂的功能。
 *
 * 使用 Job 监测协程生命周期状态：
 *  Job.isActive 协程是否处于活跃状态
 *  Job.isCancelled  协程是否处于取消状态
 *  Job.isCompleted  协程是否处于结束状态
 *
 * 使用 Job 操控协程：
 *  Job.cancel() 取消协程
 *  Job.start() 启动协程
 *
 */
class CoroutineLifecycle {

    /**
     * 直接启动协程
     */
    fun test() {
        runBlocking {
            val job = launch {
                logX("Coroutine Start")
                delay(1000)
            }
            job.log()
            job.cancel()
            job.log()
            delay(2000)

        }
    }

    /**
     * 懒加载启动协程
     *
     * Job 手动调用 cancel取消协程，生命周期状态：
     *
     * isActive = false
     * isCancelled = true
     * isCompleted = true
     */
    fun test2() {
        runBlocking {
            // 不同点，改为 懒加载启动协程
            val job = launch(start = CoroutineStart.LAZY) {
                logX("Coroutine Start")
                delay(1000)
            }

            delay(500)
            job.log()
            job.start()// 启动协程
            job.log()
            delay(500L)
            job.cancel()
            delay(500L)
            job.log()
            delay(2000L)
            logX("Process end!")

        }
    }

    /**
     * 懒加载启动协程
     *
     * 协程正常执行完毕，生命周期状态：
     *
     * isActive = false
     * isCancelled = false
     * isCompleted = true
     */
    fun test3() {
        runBlocking {
            val job = launch(start = CoroutineStart.LAZY) {
                logX("Coroutine start!")
                delay(1000L)
                logX("Coroutine end!")
            }
            delay(500L)
            job.log()
            job.start()
            job.log()
            delay(1100L) // ① 等待 Job 执行完成
            job.log()
            delay(2000L) // ② 希望 job 执行完毕后输出下面语句
            logX("Process end!")
        }
    }

    /**
     * 为了更加灵活地 "等待和监听" 协程的结束事件，我们可以使用 Job.join() 和 Job.invokeOnCompletion {}
     * 来优化 test3() 方法中的代码
     *
     * Job.join() 其实是一个挂起函数，它的作用就是：挂起当前程序执行的流程，等待 Job 当中的协程任务执行完毕，
     * 然后再恢复当前程序的执行流程。
     */
    fun test4() {
        runBlocking {
            // 模拟下载任务，耗时时间不可控
            suspend fun download() {
                val time = (Random.nextDouble() * 1000).toLong()
                logX("delay time:$time")
                delay(time)
            }

            val job = launch(start = CoroutineStart.LAZY) {
                logX("Coroutine start")
                download()
                logX("Coroutine end")
            }
            delay(500)
            job.log()
            job.start()
            job.log()
            job.invokeOnCompletion {
                // 协程执行结束后，会调用这里的代码
                logX("invokeOnCompletion...")
                job.log()
            }
            job.join() // 等待协程执行完毕
            logX("Process end")
        }
    }
}


private fun Job.log() {
    logX(
        """
            isActive:$isActive
            isCancelled:$isCancelled
            isCompleted:$isCompleted
        """.trimIndent()
    )
}

private fun logX(any: Any?) {
    println(
        """
==============================
$any
Thread:${Thread.currentThread().name}
==============================
    """.trimIndent()

    )
}