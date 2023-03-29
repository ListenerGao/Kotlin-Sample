package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis


fun main() {
    val kotlinStructure = KotlinStructure()
//    kotlinStructure.testStructure()
//    kotlinStructure.sync()
    kotlinStructure.async()
}


/**
 * Job 与结构化并发
 * 协程的优势在于结构化并发，它的重要性仅次于"挂起函数"。
 * 简单来说，"结构化并发" 就是：带有结构和层级的并发。
 *
 * 结构化并发最大的优势：我们可以实现只控制 "父协程" ，从而达到控制一堆子协程的目的。
 */
class KotlinStructure {

    fun testStructure() {
        runBlocking {
            val parentJob: Job
            var job1: Job? = null
            var job2: Job? = null
            var job3: Job? = null

            parentJob = launch {

                job1 = launch {
                    logX("job1 Coroutine start")
                    delay(1000)
                    logX("job1 Coroutine end")
                }

                job2 = launch() {
                    logX("job2 Coroutine start")
                    delay(3000)
                    logX("job2 Coroutine end")
                }

                job3 = launch {
                    logX("job3 Coroutine start")
                    delay(5000)
                    logX("job3 Coroutine end")
                }
            }

            delay(500)

            /**
             * 我们对“parentJob.children”进行了遍历，然后逐一比较它们与 job1、job2、job3 的引用是否相等。
             * （“===”代表了引用相等，即是否是同一个对象）
             * 通过比较得出：job1、job2、job3 其实就是 parentJob 的 children。也就是说，我们使用 launch 创建
             * 出来的协程是有父子关系的。
             */
            parentJob.children.forEachIndexed { index, job ->
                when (index) {
                    0 -> println("job1 === job is ${job1 === job}")  // true
                    1 -> println("job2 === job is ${job2 === job}")  // true
                    2 -> println("job3 === job is ${job3 === job}")  // true
                }
            }

            /**
             * 此处我们调用的是 parentJob 的 join() 方法，但是它会等待 job1、job2、job3 全部执行完毕，
             * 才会恢复执行。换句话说，只有当 job1、job2、job3 全部执行完毕，parentJob 才算是执行完毕了。
             *
             * 调用 parentJob 的 cancel() 方法，也是同理。job1、job2、job3 它们内部的协程任务也全都被取消了。
             *
             * 所以，当我们以结构化的方式构建协程后，我们的 join()、cancel() 等操作，也会以结构化的模式来执行。
             */
            parentJob.join()
            logX("Process end")
        }
    }

    /**
     * 同步执行
     *
     * 三个挂起函数，各自耗时 1000ms，且它们之间的运行结果也互不相干，
     * 执行完三个挂起函数需要耗时 3s 多，我们可以使用下面 async() 进行优化。
     *
     */
    fun sync() {
        runBlocking {
            suspend fun getResult1(): String {
                delay(1000L)// 模拟耗时操作
                return "Result1"
            }

            suspend fun getResult2(): String {
                delay(1000L) // 模拟耗时操作
                return "Result2"
            }

            suspend fun getResult3(): String {
                delay(1000L) // 模拟耗时操作
                return "Result3"
            }

            val results = mutableListOf<String>()
            val time = measureTimeMillis {
                results.add(getResult1())
                results.add(getResult2())
                results.add(getResult3())
            }
            println("Time: $time")
            println(results)
        }
    }

    /**
     * 异步执行
     *
     * 优化后代码，耗时 1s 多。
     *
     * async 常见的使用场景是：与挂起函数结合，优化并发。
     */
    fun async() {
        runBlocking {
            suspend fun getResult1(): String {
                delay(1000L)// 模拟耗时操作
                return "Result1"
            }

            suspend fun getResult2(): String {
                delay(1000L) // 模拟耗时操作
                return "Result2"
            }

            suspend fun getResult3(): String {
                delay(1000L) // 模拟耗时操作
                return "Result3"
            }

            val results: List<String>
            val time = measureTimeMillis {
                val result1 = async { getResult1() }
                val result2 = async { getResult2() }
                val result3 = async { getResult3() }

                results = listOf(result1.await(), result2.await(), result3.await())
            }
            println("Time: $time")
            println(results)
        }
    }
}

