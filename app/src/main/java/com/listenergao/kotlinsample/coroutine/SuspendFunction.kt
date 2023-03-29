package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


fun main() {

    val suspendFunction = SuspendFunction()
    suspendFunction.sync()


    suspend fun test(int: Int): String {
        delay(1000)
        return int.toString()
    }

    val testFun: suspend (Int) -> String = ::test

}

/**
 * 挂起函数，比普通函数多了一个 suspend 关键字。
 * 挂起函数支持挂起和恢复。
 * 挂起函数可以极大地简化异步编程，让我们能够以同步的方式写出异步代码。
 *
 * 挂起函数：只能在协程中被调用，或者是被其它挂起函数调用。
 * 原因：查看源码可知，suspend 函数的入参 Continuation，需要有一个协程上下文 CoroutineContext 信息，只有在协程
 * 作用域中才能传递。
 *
 * 协程之所以是非阻塞的，是因为它支持 "挂起和恢复"；而挂起和恢复的能力，主要来源于 "挂起函数"；
 * 而挂起函数是由 CPS 转换实现的，其中的 Continuation ，本质上就是 Callback。
 *
 * 协程和挂起函数都是支持挂起和恢复，它们两个是同一个东西吗？
 * 我们可以简单认为：挂起和恢复，是协程的一种底层能力；而挂起函数，是这种底层能力的一种表现形式，
 * 通过暴露出来的 suspend 关键字，我们开发这可以在上层，非常方便的使用这种底层能力。
 *
 *
 */
@OptIn(ExperimentalTime::class)
class SuspendFunction {


    /**
     * 同步执行，耗时 6s 多
     *
     * 表面上看起来是同步的代码，实际上也涉及到了线程切换，一行代码切换了两个线程；
     *      比如：val token = getToken()，其中 "=" 左边的代码运行在主线程，而 "=" 右边的代码运行在 IO 线程；
     *      每一次从主线程切换到 IO 线程，都是一次协程的挂起；
     *      每一次从 IO 线程切换到主线程，都是一次协程的恢复；
     *
     * 挂起和恢复是挂起函数特有的能力，不同函数不具备。
     *
     * 挂起，只是将程序执行流程转移到了其它线程，主线程不会阻塞。
     *
     * 借助挂起函数，我们可以使用同步的方式来写异步代码，对比"回调地狱"式的代码，挂起函数写出的代码可读性更好、
     * 扩展性更好、维护性更高、且不易出错。
     */
    fun sync() {
        runBlocking {
            val time = measureTime {

                val token = getToken()
                println("token:$token")
                val userInfo = getUserInfo(token)
                println("userInfo:$userInfo")
                val devices = getDevices(userInfo)
                println("devices:$devices")
                val log = """
                    sync:
                    token:$token,
                    userInfo:$userInfo,
                    devices:$devices
            """.trimIndent()
                println(log)
            }
            println("sync cost time:$time")


        }
    }


    /**
     * 挂起函数的本质：就是 Callback，当 kotlin 检测到 suspend 关键字修饰的函数时，
     * 就会自动将挂起函数转换成带有 Callback 的函数。
     *
     * getToken() 反编译成 java：
     * // Continuation 相当于 Callback
     * public final Object getToken(@NotNull Continuation var1) {
     *      ...
     *      return ""Token
     * }
     *
     * 我们看一下 Continuation 在 kotlin 中的定义
     * @SinceKotlin("1.3")
     * public interface Continuation<in T> {
     *
     *      // The context of the coroutine that corresponds to this continuation.
     *
     *      public val context: CoroutineContext
     *
     *
     *      // Resumes the execution of the corresponding coroutine passing a successful or failed [result] as the
     *      // return value of the last suspension point.
     *
     *      public fun resumeWith(result: Result<T>)
     * }
     * 通过源码我们可以发现，Continuation 本质上是一个带有泛型参数的 Callback。
     * 这个"从挂起函数转换成 Callback 函数"的过程，叫做 CPS 转换（Continuation-Passing-Style Transformation）
     *
     * Continuation 表示"程序继续运行下去需要执行的代码"，"接下来要执行的代码"，"剩下的代码"
     *
     *          *******************
     *          *    val token =  *  getToken()
     *          *                 **************************
     *          *    println("token:$token")               *
     *          *    val userInfo = getUserInfo(token)     *
     *          *    println("userInfo:$userInfo")         *
     *          *    val devices = getDevices(userInfo)    *
     *          *    println("devices:$devices")           *
     *           *******************************************
     *
     *           如上图所示，当程序运行到 getToken() 这个挂起函数时，它的 Continuation 就是 * 圈起的框。
     *
     * 我们理解 Continuation 以后，CPS就容易理解了，它其实就是"将程序加下来要执行的代码进行了传递的一种模式"，
     * CPS转换，本质上就是"将原来的的同步挂起函数转换成 Callback 异步代码"的过程，这个转换过程是编译器在背后做的，
     * 我们程序对此并无感知。
     *
     *
     */
    private suspend fun getToken(): String {
        println("1:getToken start")
        withContext(Dispatchers.IO) {
            println("2:getToken withContext start")
            delay(2000)
            println("3:getToken withContext end")
        }
        println("4:getToken end")
        return "Token"
    }

    private suspend fun getUserInfo(token: String): String {
        println("getUserInfo start")
        withContext(Dispatchers.IO) {
            println("getUserInfo withContext start")
            delay(2000)
            println("getUserInfo withContext end")
        }
        println("getUserInfo end")
        return "UserInfo"
    }

    private suspend fun getDevices(userInfo: String): String {
        println("getDevices start")
        withContext(Dispatchers.IO) {
            println("getDevices withContext start")
            delay(2000)
            println("getDevices withContext end")
        }
        println("getDevices end")
        return "Devices"
    }
}