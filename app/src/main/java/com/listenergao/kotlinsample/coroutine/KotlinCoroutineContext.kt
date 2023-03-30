package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


fun main() {
    val kotlinCoroutineContext = KotlinCoroutineContext()
    kotlinCoroutineContext.test()
}


/**
 * Kotlin 协程上下文
 *
 * CoroutineContext 协程上下文，实际开发中它最常见的用处就是切换线程池。
 * 我们在查看 launch/runBlocking 源码时，CoroutineContext 就是函数的第一个参数，
 * 默认实现为 EmptyCoroutineContext。我们也可以传入 Dispatcher 作为参数。
 *
 * Dispatchers 相关参数：
 *
 * Dispatchers.Main：
 * 它只有在 UI 编程平台上才有意义，在 Android、Ios 之类的平台上，Main 线程才能用于 UI 绘制。
 * Dispatchers.Io：
 * 它是用于执行 IO 密集型任务的线程池。它内部的线程池数量会比较多一些，比如 2N （N 为 CPU 核心数），
 * 具体的线程数量可以通过参数来配置：kotlinx.coroutines.io.parallelism。
 * Dispatchers.Default：
 * 它是用于执行 CPU 密集型任务的线程池。一般来说，它内部的线程数量与机器的 CPU 核心数保持一致，
 * 不过它有一个最小限制 2。
 * Dispatchers.Unconfined：
 * 表示当前协程可以运行在任意线程上。
 *
 * 注意：Dispatchers.IO 底层是可能复用 Dispatchers.Default 当中的线程的。
 * 当 Dispatchers.Default 线程池当中有富余线程的时候，它是可以被 IO 线程池复用的。
 *
 *
 * 在 kotlin 中，但凡时重要的概念，都或多或少跟 CoroutineContext 有关系：Job、Dispatcher、
 * CoroutineExceptionHandler、CoroutineScope，甚至挂起函数，它们都跟 CoroutineContext 有着密切的联系。
 * 其中 Job、Dispatcher、CoroutineExceptionHandler 本身，就是 Context。
 */
class KotlinCoroutineContext {

    fun test() {
        runBlocking {
            val userInfo = getUserInfo()
            logX(userInfo)

        }
    }

    private suspend fun getUserInfo(): String {
        logX("Before IO Context")
        withContext(Dispatchers.IO) {
            logX("In IO Context")
            delay(1000L)
        }
        logX("After IO Context")

        return "UserInfo"
    }
}