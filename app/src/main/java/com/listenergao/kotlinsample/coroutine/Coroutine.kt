package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.*


fun main() {

    val coroutine = Coroutine()
//    coroutine.startOne()
//    coroutine.startTwo()
    coroutine.startThree()

}


/**
 * 协程
 * 1、广义的协程，可以理解成"互相协作的程序"；
 * 2、协程框架，是独立与 kotlin 标准库的一套框架，它封装了 java 的线程，并对开发者暴露了协程相关的 API；
 * 3、程序中运行的协程，可以理解为 "轻量级的线程"；
 * 4、一个线程中，可以运行成千上万个协程；
 * 5、协程，也可以理解为运行在线程当中的非阻塞的 Task；
 * 6、协程，通过挂起和恢复的能力，实现了 "非阻塞"；
 * 7、协程不会与特定线程绑定，它可以在不同线程之间灵活切换，而这其实是通过 "挂起和恢复" 来实现的。
 *
 *
 * 设置 VM 参数，可以打印出协程信息
 * -Dkotlinx.coroutines.debug
 */
class Coroutine {

    /**
     * launch 启动协程
     *
     * GlobalScope.launch{} 它是一个高阶函数，主要用来启动一个协程。
     * GlobalScope 是 kotlin 官方为我们提供的"协程作用域"
     *
     * delay() 延迟，它是一个挂起函数，拥有挂起和恢复的能力，可以实现非阻塞。
     *
     * launch 启动协程后，无法返回执行结果。
     * 打个比方，launch 一个协程任务，就像猎人射箭一样。
     * launch 和 射箭 的共同点：
     *      1、箭一旦射出去，就无法改变目标；协程一旦被 launch，那么它执行的任务也不会被中途改变。
     *      2、箭如果命中了猎物，猎物也不会自动送到我们手上来；launch 的协程任务一旦完成了，即使有结果，
     *      也没办法直接返回给调用方。
     *
     * launch 源码
     * public fun CoroutineScope.launch(
     *      // 协程上下文，我们可以传入 Dispatcher，来指定协程运行的线程池。
     *      context: CoroutineContext = EmptyCoroutineContext,
     *      // 协程启动模式，CoroutineStart 是一个枚举类一共四种：
     *      // DEFAULT（立即执行）、LAZY（懒加载执行）、ATOMIC（）、UNDISPATCHED（）
     *      start: CoroutineStart = CoroutineStart.DEFAULT,
     *      // CoroutineScope 的成员方法或扩展函数，无参数类型，无返回值类型
     *      block: suspend CoroutineScope.() -> Unit
     * ): Job {...}
     * 首先 CoroutineScope.launch 代表 launch 是一个扩展函数，而它的 "扩展接收者类型" 是 CoroutineScope。
     * 这意味着 launch() 等价于 CoroutineScope 的成员方法。而如果我们想要启动协程，就必须先拿到 CoroutineScope
     * 的对象。我们使用 GlobalScope ，其实就是 kotlin 官方为我们提供的一个 CoroutineScope 对象，
     * 方便我们直接启动协程。
     *
     *
     * GlobalScope 不建议使用。
     */
    fun startOne() {
        GlobalScope.launch {
            println("Coroutine start")
            delay(2000)
            println("hello world")
        }
        println("after launch...")
        // 睡眠 2s 为了不让主线程结束，避免通过 launch 创建的协程还没来得及开始执行，整个程序就已经结束了。
        Thread.sleep(3000)
        println("main thread end...")
    }

    /**
     * runBlocking 启动协程
     *
     * 它与 launch 行为模式不太一样，它存在某种阻塞行为，可以从协程当中返回执行结果。
     *
     *
     * runBlocking 源码：
     * public fun <T> runBlocking(
     *      context: CoroutineContext = EmptyCoroutineContext,
     *      block: suspend CoroutineScope.() -> T
     * ): T {...}
     *
     * runBlocking 是一个顶层函数，它并不是 CoroutineScope 的扩展函数，因此我们调用它的时候，
     * 不需要 CoroutineScope 的对象。
     * runBlocking 是对 launch 的补充，由于它是阻塞式的，并不适用于实际的开发工作中。
     *
     */
    fun startTwo() {
        runBlocking {
            println("Coroutine start")
            delay(2000)
            println("hello world")
        }
        println("after launch...")
        // 睡眠 2s 为了不让主线程结束，避免通过 launch 创建的协程还没来得及开始执行，整个程序就已经结束了。
        // 由于 runBlocking 是阻塞的，此处就不要睡眠了。
//        Thread.sleep(3000)
        println("main thread end...")
    }


    /**
     * async 启动协程
     *
     * async{} 的返回值，是一个 Deferred（继承 Job） 对象，我们可以通过它的 await() 方法，就可以拿到协程执行的结果。
     * 对比 launch 像"射箭"一样，这里的 async 更像是"钓鱼"。
     * 我们手里的鱼竿，就有点像 async 当中的 Deferred 对象。一旦鱼上钩了，我们就可以直接拿到结果。
     */
    fun startThree() {
        runBlocking {
            println("1:In runBlocking:${Thread.currentThread().name}")

            val deferred: Deferred<String> = async {
                println("3:In async:${Thread.currentThread().name}")
                delay(2000)
                return@async "Task Completed"
            }

            println("2:After async:${Thread.currentThread().name}")

            // 即使不调用 await 方法，async 中的代码块也是会执行的，
            // 就好比钓鱼，鱼钩已经扔出去了，钓鱼这个动作已经开始了，只是我没有拉杆。
            val result: String = deferred.await()
            println("4:result:$result")

        }
    }
}