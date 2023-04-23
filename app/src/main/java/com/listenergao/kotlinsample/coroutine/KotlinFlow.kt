package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

fun main() {
    val kotlinFlow = KotlinFlow()
//    kotlinFlow.createFlow()
//    kotlinFlow.flowLifecycle()
//    kotlinFlow.testFlowOnCompletion()
//    kotlinFlow.processFlowException()
//    kotlinFlow.switchFlowContextWithFlowOn()
//    kotlinFlow.switchFlowContextWithLaunchIn()
//    kotlinFlow.testFlow()
//    kotlinFlow.testFlowTwo()
//    kotlinFlow.testFlowSample()
    kotlinFlow.testFlowSampleTwo()
}

/**
 * Flow 在 kotlin 协程当中自成体系的知识点，它极其强大灵活。
 * 自从 2019 年 kotlin 推出 Flow 后，kotlin 协程已经没有明显的短板了。
 * 简单的异步操作，我们可以直接使用挂起函数、launch、async；复杂的场景，我们可以使用 Flow。
 *
 * Flow 在 kotlin 协程中，就是 “数据流” 的意思。因为 Flow 当中 “流淌” 的，都是数据。
 * Flow 和 Channel 不一样，Flow 并不是只有 “发送” 和 “接收” 两个行为，它当中流淌的数据是可以在中途改变的。
 *
 * Flow 的数据发送方，我们称之为 “上游”；数据接收方称之为 “下游”。中途的数据操作，我们可以称之为 ”中转站“。
 */
class KotlinFlow {


    /**
     * 创建 Flow 的三种方式：
     *
     * 1，flow{} ：       适用于 未知数据集
     * 2，flowOf() ：     适用于 已知具体的数据
     * 3，asFlow() ：     适用于 已知数据来源的集合
     */
    fun createFlow() = runBlocking {

        /**
         * flow {} :是一个高阶函数，它的作用就是创建一个新的 Flow，
         * 在它的 lambda 中，我们可以使用 emit() 这个挂起函数往下游发送数据，
         * 这里的 emit 其实就是 “发射”、“发送” 的意思，
         * 上游创建了一个 “数据流” ，同时也要负责发送数据。
         *
         * filter{}, map{}, take(), 它们是中间操作符，就像中转站一样，对数据进行处理。Flow 的操作符跟集合的
         * 操作符高度相似，只要你会 List，Sequence，那么你可以快速上手 Flow 操作符。
         * 并且 List 和 Flow 可以互转：
         * List -> Flow : flowOf(1,2,3).toList()
         * Flow -> List : listOf(1,2,3).asFlow()
         *
         * collect{} ：被称为 “终止操作符” 或者 “末端操作符”，它的作用只有一个：终止 Flow 数据流，并接收数据。
         */
        flow {// 上游，数据发源地
            emit(1)         // 挂起函数
            emit(2)
            emit(3)
            emit(4)
            emit(5)
            emit(6)
        }
            .filter { it > 2 }      // 中转站 1
            .map { it * 2 }         // 中转站 2
            .take(3)         // 中转站 3
            .collect {        //
                println("flow collect data:$it")
            }

        flowOf(1, 2, 3, 4, 5, 6)
            .filter { it > 2 }
            .map { it * 2 }
            .take(3)
            .collect {
                println("flowOf collect data:$it")
            }

        listOf(1, 2, 3, 4, 5, 6)
            .asFlow()
            .filter { it > 2 }
            .map { it * 2 }
            .take(3)
            .collect {
                println("list asFlow collect data:$it")
            }
    }

    /**
     * Flow 的中间操作符当中，有两个是比较特殊的，onStart、onCompletion 它们是以操作符的形式存在，实际上的作用
     * 是监听 Flow 生命周期的回调。
     * onStart、onCompletion 的执行顺序，并不是严格按照上下游来执行的，虽然 onStart 是处于最下游的，但它是最先
     * 被执行的；onCompletion 只会在 Flow 数据流执行完毕以后，才会被回调。
     *
     * onCompletion{} 的回调情况可看
     * @see testFlowOnCompletion()
     *
     */
    fun flowLifecycle() = runBlocking {

        flowOf(1, 2, 3, 4, 5)
            .filter {
                println("filter: $it")
                it > 2
            }
            .map {
                println("map: $it")
                it * 2
            }
            .take(3)
            .onCompletion {
                println("onCompletion...")
            }
            .onStart {
                println("onStart...")
            }
            .collect {
                println("collect data:$it")
            }
    }

    /**
     * onCompletion{} 在以下三种情况中都会进行回调：
     * 1，Flow 正常执行完毕；
     * 2，Flow 当中出现异常；
     * 3，Flow 被取消；
     *
     * @see flowLifecycle() 可查看正常执行完毕 onCompletion 的回调；
     *
     *
     */
    fun testFlowOnCompletion() = runBlocking {
        launch {

            flow {
                emit(1)
                emit(2)
                emit(3)
            }
                .onCompletion {
                    println("onCompletion first:$it")
                }
                .collect {
                    println("collect: $it")
                    if (it == 2) {
                        cancel()
                        println("flow cancel")
                    }
                }
        }

        delay(1000L)

        flowOf(4, 5, 6)
            .onCompletion {
                println("onCompletion second:$it")
            }
            .collect {
                println("collect: $it")
                throw IllegalStateException()
            }
    }

    /**
     * Flow 异常处理
     * Flow 主要有三个部分：上游、中间操作、下游。那么 Flow 中的异常，也可以按照这个标准来处理，也就是异常发生的位置。
     *
     * 对于发生在 上游 和 中间操作 过程中异常，我们可以使用 Flow 提供的 catch 操作符来进行捕获和进一步处理。
     * 需要注意的是，catch 操作符与它的位置强相关。
     * catch 作用域，仅限于 catch 的上游。也就是说，发生在 catch 上游的异常，才会被捕获，发生在 catch 下游的
     * 异常则不会被捕获。
     *
     * 下游的异常可以使用 try-catch 进行捕获。
     */
    fun processFlowException() = runBlocking {

        val flow = flow {
            emit(1)
            emit(2)
            throw IllegalStateException() // 主动抛出异常
            emit(3)
        }

        flow.map { it * 2 }
            // 使用 catch 操作符捕获异常
            .catch {
                println("catch: $it")
            }
            .collect {
                // 下游的话，就必须使用 try-catch 来捕获异常了
                try {
                    println("collect: $it")
                    throw IllegalStateException()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

    }

    /**
     * 切换 Context：flowOn、launchIn
     * Flow 非常适合复杂的异步任务，在大部分异步任务中，我们都需要频繁切换工作的线程。我们可以借助 flowOn 或者
     * launchIn 操作符来完成。
     *
     * flowOn 操作符也是和它的位置强相关，和 catch 类似，flowOn 仅限于它的上游。
     *
     */

    // 自定义协程 Context
    private val mySingleDispatcher = Executors.newSingleThreadExecutor {
        Thread(it, "MySingleThread").apply { isDaemon = true }
    }.asCoroutineDispatcher()

    fun switchFlowContextWithFlowOn() = runBlocking {
        flow {
            logX("start")
            emit(1)
            logX("emit 1")
            emit(2)
            logX("emit 2")
            emit(3)
            logX("emit 3")
        }
            .map {
                logX("before map: $it")
                it * 2
            }
            .flowOn(Dispatchers.IO)
            .collect {
                // 指定 collect 当中的 Context 为 MySingleThread，可以使用 launchIn 代替，代码更简洁。
                withContext(mySingleDispatcher) {
                    logX("collect: $it")
                }
//                logX("collect $it")
            }
    }

    fun switchFlowContextWithLaunchIn() = runBlocking {
        val flow = flow {
            logX("start")
            emit(1)
            logX("emit 1")
            emit(2)
            logX("emit 2")
            emit(3)
            logX("emit 3")
        }

        val scope = CoroutineScope(mySingleDispatcher)
        flow.flowOn(Dispatchers.IO)
            .map {
                logX("before map: $it")
                it * 2
            }
            .onEach {
                logX("onEach: $it")
            }
            .launchIn(scope)

        // 延迟 1s，便于输出日志
        delay(1000)
    }

    /**
     * Flow 是 “冷” 的
     *
     * Flow 只有调用了终止操作符之后，才会开始工作；
     * Channel 不管有没有接收方，发送方都会工作；
     */
    fun testFlow() = runBlocking {
        // 冷数据流
        val flow = flow {
            (1..3).forEach {
                println("flow before send : $it")
                emit(it)
                println("flow end : $it")
            }
        }

        // 热数据流
        val channel = produce<Int>(capacity = 0) {
            (1..3).forEach {
                println("channel before send : $it")
                send(it)
                println("channel send : $it")
            }
        }

        println("finish")
    }

    /**
     * Flow 不仅是 “冷” 的，还是 “懒” 的。
     *
     * 运行结果：
     * emit 3
     * filter: 3
     * map: 3
     * collect: 6
     * emit 4
     * filter: 4
     * map: 4
     * collect: 8
     * emit 5
     * filter: 5
     * map: 5
     * collect: 10
     * 从运行结果中可以看出，Flow 一次只会处理一条数据。虽然这也是 Flow “冷” 的一种表现，但这个特性准确来说是 “懒” 。
     *
     * 对比 Channel 的思维模型来看的话：
     *   -------------------------------------------------------------------------------------------------------------------------------------------------
     *  ｜    Type   ｜                 服务员                   ｜          自来水厂           ｜         优势         ｜                劣势               ｜
     *  ｜-------------------------------------------------------------------------------------------------------------------------------------------------
     *  ｜  Channel  ｜           热情、主动端茶送水              ｜      不管用不用水，都工作     ｜       响应速度快      ｜   资源浪费，数据是提前准备好的旧数据   ｜
     *  ｜-------------------------------------------------------------------------------------------------------------------------------------------------
     *  ｜   Flow    ｜  冷谈、懒惰，你找服务员要，服务员才会送过来  ｜  只有你用水的时候，才会送过来  ｜  数据是新的，节省资源  ｜               响应速度慢            ｜
     *  ｜-------------------------------------------------------------------------------------------------------------------------------------------------
     *
     * 注意：Flow 默认情况下是 “懒惰” 的，但也可以通过配置让它 “勤快” 起来。
     */
    fun testFlowTwo() = runBlocking {
        flow {
            println("emit 3")
            emit(3)
            println("emit 4")
            emit(4)
            println("emit 5")
            emit(5)
        }
            .filter {
                println("filter: $it")
                it > 2
            }
            .map {
                println("map: $it")
                it * 2
            }
            .collect {
                println("collect: $it")
            }
    }

    fun testFlowSample() = runBlocking {

        fun loadData() = flow {
            repeat(3) {
                delay(500L)
                emit(it)
                logX("emit: $it")
            }
        }

        fun updateUi(it: Int) {
            logX("updateUi: $it")
        }

        fun showLoading() {
            logX("showLoading...")
        }

        fun hideLoading() {
            logX("hideLoading...")
        }

        // 模拟主线程
        val uiScope = CoroutineScope(mySingleDispatcher)


        loadData().onStart {
            showLoading()
        }
            .map { it * 2 }
            .flowOn(Dispatchers.IO)
            .catch {
                logX("throwable:$it")
                emit(-1)
                hideLoading()
            }
            .onEach {
                updateUi(it)
            }
            .onCompletion {
                hideLoading()
            }
            .launchIn(uiScope)


        // 延时 2s 输出日志
        delay(2000)

    }

    /**
     * 计时器功能
     * 模拟流速不均匀问题，上游每隔 1s 发送一条数据，下游处理数据需要花费 3s，我们通过日志可以观察到，
     * 计时器每 3s 才更新一次数据，这样计时器就不准确了。
     * 该问题的本质是下游处理数据速度过慢，导致管道中存在大量积压的数据，并且积压的数据会一个个传递到下游，
     * 即使这些数据已经过期了。
     *
     * 由于 Flow 是基于观察者模式的响应式编程，上游发送了一个数据，下游就会接收到一个数据。但是下游处理数据的速度
     * 不一定和上游发送数据的速度是一致的，如果下游处理速度过慢，就可能出现管道阻塞的情况。
     *
     * 响应式编程框架都可能遇到这种问题，RxJava 中有专门的背压策略来处理这类问题。Flow 中也有此类问题的处理方案，
     * 此案例中，我们使用一个简单的方式来解决这个问题。我们使用 Flow 提供的 collectLatest{} 来解决即可。
     * collectLatest{} 只接收处理最新的数据。如果有新数据到来，而前一个数据还没有处理完，则会将前一个数据剩余
     * 的处理逻辑全部取消。
     *
     */
    fun testFlowSampleTwo() = runBlocking {
        var time = 0
        flow {
            while (true) {
                emit(time)
                delay(1000)
                time++
            }
        }
            // 存在背压问题
//            .collect {
//                println("计时器更新: $it")
//                delay(3000L)
//            }
            .collectLatest {
                println("计时器更新: $it")
                delay(3000L)
            }
    }
}