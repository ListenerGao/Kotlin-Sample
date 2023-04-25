package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.runBlocking
import java.time.LocalTime


fun main() {
    val flowOperator = FlowOperator()
//    flowOperator.testOperator()
//    flowOperator.testOperatorDebounce()
//    flowOperator.testOperatorSample()
//    flowOperator.testOperatorReduce()
//    flowOperator.testOperatorFold()
//    flowOperator.testOperatorFlatMapConcat()
//    flowOperator.testOperatorFlatMapMerge()
    flowOperator.testOperatorFlatMapLatest()
}

/**
 * Flow 操作符
 */
class FlowOperator {

    /**
     * map：映射。将一个值映射成另一个值，具体的映射规则在 map 函数中自定定义。
     * filter：过滤。根据在 filter 函数中定义的过滤规则，过滤掉一些数据。
     * onEach：遍历数据。
     */
    fun testOperator() = runBlocking {
        flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .onEach {
                println("onEach: $it")
            }
            .filter {
                it % 2 == 0
            }
            .map {
                it * 2
            }
            .collect {
                println("collect: $it")
            }
    }

    /**
     * debounce：用来确保 Flow 的各项数据之间存在一定时间间隔，如果时间点过于临近的数据，只会保留最后一条。
     */
    @OptIn(kotlinx.coroutines.FlowPreview::class)
    fun testOperatorDebounce() = runBlocking {
        flow {
            emit(1)
            emit(2)
            delay(600)
            emit(3)
            delay(100)
            emit(4)
            delay(200)
            emit(5)
        }
            .debounce(500) // 两条数据之间间隔 500ms，输出结果为 2，5
            .collect {
                println("collect: $it")
            }
    }

    /**
     * sample：采样的意思。可以从 Flow 的数据流中按照一定的时间间隔来采样某一条数据。
     *
     * 模拟弹幕发送
     */
    @OptIn(kotlinx.coroutines.FlowPreview::class)
    fun testOperatorSample() = runBlocking {

        flow {
            while (true) {
                emit("发来一条弹幕...")
            }
        }
            .sample(1000) // 每隔 1s 采样一条数据
            .flowOn(Dispatchers.IO) // 由于 flow 是通过死循环不断发送数据，须调用 flowOn 函数，让其运行在 IO 线程，否则主线程会一直被卡死。
            .collect {
                println("time:${LocalTime.now()} : $it")
            }
    }

    /**
     * reduce ：终端操作符
     *
     * flow.reduce { accumulator, value -> accumulator + value }
     * accumulator ：累积值的意思
     * value ：当前值的意思
     *
     * 也就是说，reduce 函数会通过参数给我们一个 Flow 的累积值和一个 Flow 的当前值，
     * 我们可以在函数体中对它们进行一定的运算，运算的结果会作为下一个累积值继续传递到 reduce 函数当中。
     *
     * 模拟计算 1-100 的和
     */
    fun testOperatorReduce() = runBlocking {
        val result = flow {
            (1..100).forEach {
                emit(it)
            }
        }
            .reduce { accumulator, value -> accumulator + value }
        println("flow result: $result")
    }


    /**
     * fold ：终端操作符
     * 和 reduce 基本上完全相似，只不过 fold 函数需要传入一个初始值。
     *
     * flow.fold(initial) { acc, value -> acc + value }
     */
    fun testOperatorFold() = runBlocking {
        val result = flow {
            ('A'..'Z').forEach {
                emit(it)
            }
        }.fold("Alphabet:") { acc, value -> acc + value }
        println(result)
    }

    /**
     * flatMapConcat : 将两个 Flow 中的数据进行映射、合并、压平成一个 Flow，最后进行输出
     *
     * 适合用于有关联的多个请求，例如：首先登录后获取用户 Token，根据 Token 获取用户详细信息。
     * 可以解决嵌套地狱的问题。
     */
    fun testOperatorFlatMapConcat() = runBlocking {
        flowOf(1, 2, 3)
            .flatMapConcat {// 此处使用 flatMapConcat 或 flatMapMerge 结果是一样的。
                flowOf("a$it", "b$it")
            }
            .collect {
                println("collect: $it")
            }
    }

    /**
     * flatMapMerge ：内部启用并发处理数据，不会保证最终结果的顺序。
     *
     * flatMapConcat 和 flatMapMerge 的效果好像是一样，在上面测试中，执行结果是一样的。
     * @see testOperatorFlatMapConcat()
     *
     * 我们从下面测试的结果可以发现：
     * flatMapConcat ：是按照 Flow 发送数据的顺序执行的，即使第一个数据 delay(300) ，后面的数据也没有优先执行权。
     * flatMapMerge ：它是并发执行数据的，并不保证顺序，哪条数据 delay 的时间短，它就可以优先被执行。
     *
     */
    fun testOperatorFlatMapMerge() = runBlocking {
        /**
         * flatMapConcat 执行结果
         * collect: a300
         * collect: b300
         * collect: a200
         * collect: b200
         * collect: a100
         * collect: b100
         */
        flowOf(300, 200, 100)
            .flatMapConcat {
                flow {
                    delay(it.toLong())
                    emit("a$it")
                    emit("b$it")
                }
            }
            .collect {
                println("collect: $it")
            }

        /**
         * flatMapMerge 执行结果
         * collect: a100
         * collect: b100
         * collect: a200
         * collect: b200
         * collect: a300
         * collect: b300
         *
         */
        flowOf(300, 200, 100)
            .flatMapMerge {
                flow {
                    delay(it.toLong())
                    emit("a$it")
                    emit("b$it")
                }
            }
            .collect {
                println("collect: $it")
            }
    }

    /**
     * flatMapLatest ：和其它两个 flatMap 函数都是类似的，也是将两个 Flow 合并、压平成一个 Flow。
     * 它的行为和 collectLatest 函数比较接近。collectLatest 只接收处理最新的数据，如果有新的数据到来了
     * 而前一个数据还没有处理完，则会将前一个数据剩余的处理逻辑全部取消。
     *
     * flatMapLatest 也是类似的，flow1 中的数据传递到 flow2 中会立即进行处理。如果 flow1 中的下一个数据就要发送了，
     * flow2 中上一个数据还没有处理完，则会将剩余逻辑取消掉，开始处理最新的数据。
     *
     * 下面输出结果为：1，3
     */
    fun testOperatorFlatMapLatest() = runBlocking {
        flow {
            emit(1)
            delay(150)
            emit(2)
            delay(50)
            emit(3)
        }.onEach {// 查看数据发送
            println("onEach: $it")
        }
            .flatMapLatest {
                flow {
                    delay(100)
                    emit(it)
                }
            }.collect {
                println("collect: $it")
            }
    }
}