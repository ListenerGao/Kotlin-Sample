package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
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
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking
import java.time.LocalTime
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


fun main() {
    val flowOperator = FlowOperator()
//    flowOperator.testOperator()
//    flowOperator.testOperatorDebounce()
//    flowOperator.testOperatorSample()
//    flowOperator.testOperatorReduce()
//    flowOperator.testOperatorFold()
//    flowOperator.testOperatorFlatMapConcat()
//    flowOperator.testOperatorFlatMapMerge()
//    flowOperator.testOperatorFlatMapLatest()
//    flowOperator.testOperatorZip()
    flowOperator.testOperatorBuffer()
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

    /**
     * zip ：也是作用在两个 Flow 上，使用 zip 连接的两个 flow 是并行运行的关系。
     * 这点和 flatMap 差别很大，因为 flatMap 的运行方式是一个 flow 中的数据流向另一个 flow，是串行关系。
     *
     * zip 函数的规则是，只要其中一个 flow 中的数据全部处理完毕，就会终止运行，剩余未处理的数据将不会得到处理。
     * 如下面的例子中，flow1 中的 4、5 两个值就会被丢弃。
     *
     * 验证两个 flow 是并行的：
     * flow1 中延时 3s，flow2 中延时 2s，如果是串行的话，花费时间应该在 5s 以上。
     * 实际运行中，代码耗时时间在 3s 左右，由此可证明 flow1 和 flow2 是并行关系，最终的耗时取决于运行耗时更久的那个 flow。
     */
    @OptIn(ExperimentalTime::class)
    fun testOperatorZip() = runBlocking {
        // 计算耗时时间
        val costTime = measureTime {
            val flow1 = flow {
                emit(1)
                emit(2)
                delay(3000)
                emit(3)
                emit(4)
                emit(5)
            }
            val flow2 = flow {
                emit("A")
                emit("B")
                delay(2000)
                emit("C")
            }

            flow1.zip(flow2) { values1, value2 ->
                value2 + values1
            }.collect {
                println("time:${LocalTime.now()}  collect:$it")
            }
        }

        println("costTime: $costTime")

    }

    fun testOperatorBuffer() = runBlocking {

        /**
         * 测试发送三条数据，每条数据间隔 1s ，在 collect 函数中逐个对数据进行处理，处理每条数据耗时 1s。
         * 我们在 collect 函数中处理数据耗时 1s，flow 中发送数据同样要等待 1s。collect 函数处理完成数据之后，
         * flow 函数恢复运行，发现又要等待 1s，这样 2s 中才能发送一条数据。
         * 从测试结果中可以看出，collect 函数中的数据处理是会 flow 函数中的数据发送产生影响。默认情况下，collect 函数
         * 和 flow 函数是运行在同一个协程中，因此 collect 函数没有执行完毕，flow 函数中的代码也会挂起等待。
         *
         * 不知道上述行为是否是你想要的，如果不是的话，可以借助 buffer 函数就能实现另外一种你想要的行为。
         *
         */
        fun testSample() = runBlocking {
            flow {
                emit(1)
                delay(1000)
                emit(2)
                delay(1000)
                emit(3)
            }.onEach {
                println("${LocalTime.now()}  $it is ready")
            }.collect {
                delay(1000)
                println("${LocalTime.now()}  $it is handled")
            }
        }

//        testSample()


        /**
         * buffer 函数会让 flow 函数和 collect 函数运行在不同的协程当中，这样 flow 中的数据发送就不会受 collect 函数的影响了。
         * 因为有了 buffer 的存在，数据发送和数据处理变得互不干扰。
         *
         * buffer 函数其实就是一种背压的处理模式，它提供了一份缓存区，当 flow 数据流速不均匀时，使用这份缓存
         * 来保证程序的运行效率。
         *
         * flow 函数只管发送自己的数据，它并不关心数据有没有被处理，反正都缓存在 buffer 当中。
         * 而 collect 函数只需要一直从 buffer 中获取数据进行处理就可以了。
         *
         * 如果流速不均匀问题持放大，缓存区的内容越来越多时，又该怎们办？这时，我们需要引入一种新的背压策略，
         * 适当的丢弃一些数据。可以使用 conflate 函数。
         */
        fun testSampleBuffer() = runBlocking {
            flow {
                emit(1)
                delay(1000)
                emit(2)
                delay(1000)
                emit(3)
            }.onEach {
                println("${LocalTime.now()}  $it is ready")
            }
                .buffer() // 不同点
                .collect {
                    delay(1000)
                    println("${LocalTime.now()}  $it is handled")
                }
        }

        testSampleBuffer()


    }


}