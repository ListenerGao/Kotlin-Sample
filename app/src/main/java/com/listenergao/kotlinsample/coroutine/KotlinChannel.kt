package com.listenergao.kotlinsample.coroutine

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() {
    val kotlinChannel = KotlinChannel()
//    kotlinChannel.testChannel()
//    kotlinChannel.testChannelCapacity()
//    kotlinChannel.testChannelCapacityTwo()
//    kotlinChannel.testChannelBuffer()
//    kotlinChannel.testChannelBufferTwo()
//    kotlinChannel.testChannelUndeliveredElement()
//    kotlinChannel.testChannelProduce()
//    kotlinChannel.testChannelReceive()
    kotlinChannel.testChannelSendData()
}

/**
 * kotlin 协程之 Channel
 * 我们之前学习的挂起函数 async，它们一次只能返回一个结果。但在某些业务场景下，我们往往需要协程返回多个结果。
 * 这种场景下，直接使用协程就无法直接解决了。
 * 而我们今天学习的 kotlin 协程中的 Channel，就是专门用来做这种事情的。
 *
 * Channel 顾名思义，就是一个管道。
 * Channel 这个管道的其中一端是发送方，管道的另一端是接收方。而管道本身则可以用来传输数据。
 *
 *
 * public fun <E> Channel(
 *      capacity: Int = RENDEZVOUS,
 *      onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
 *      onUndeliveredElement: ((E) -> Unit)? = null
 * ): Channel<E>
 * 创建 Channel()，它是一个顶层函数，这个函数带有一个泛型参数 E，另外还有三个参数：
 * capacity：
 *      表示管道的容量。
 *      可选参数值为：
 *              RENDEZVOUS：表示容量为 0
 *              UNLIMITED：表示无限容量
 *              CONFLATED：表示容量为 1，新的数据会替代旧的数据。
 *              BUFFERED：表示具备了一定的缓存容量，默认是 64，具体容量由这个 VM 参数决定 "kotlinx.coroutines.channels.defaultBuffer"。
 *
 * onBufferOverflow：
 *      当我们指定了 capacity 的容量，等管道的容量满了时，Channel 的对应策略是怎么样的。
 *      可选参数值为：
 *              SUSPEND：当管道的容量满了之后，发送方还要继续发送，我们就会挂起当前的 send() 方法。
 *                       由于它是它是一个挂起函数，所以我们可以以非阻塞的方式，将发送方的执行流程挂起，
 *                       等管道中有了空闲位置以后再恢复。
 *              DROP_OLDEST：丢弃最旧那条数据，发送新的数据。
 *              DROP_LATEST：丢弃最新的数据。注意，这个动作的含义是丢弃当前正准备发送的那条数据，而管道中的数据维持不变。
 *
 * onUndeliveredElement：
 *      异常回调处理。当管道中某些数据没有被成功接收时，这个回调会被调用。
 *
 * Channel 本身就是一个接口，它本身没有方法和属性，它的能力来自继承的 SendChannel 和 ReceiveChannel 这两个接口。
 * 我们可以借助它的特点，实现 “对读取开放，对写入封闭” 的设计。
 */
class KotlinChannel {

    /**
     *
     * receive: 1
     * Thread:main @coroutine#3
     * send: 1
     * Thread:main @coroutine#2
     * send: 2
     * Thread:main @coroutine#2
     * receive: 2
     * Thread:main @coroutine#3
     * receive: 3
     * Thread:main @coroutine#3
     * send: 3
     * Thread:main @coroutine#2
     *
     * 通过测试结果，我们首先看到的是：@coroutine#2，@coroutine#3，两个协程在交替运行。
     */
    fun testChannel() = runBlocking {
        // 1: 创建管道
        val channel = Channel<Int>()

        launch {
            // 2: 在一个单独的协程里发送管道消息
            (1..3).forEach {
                channel.send(it)
                logX("send: $it")
            }
            // Channel 也是一种协程资源，用完之后需要主动关闭，避免造成资源浪费。
            // 如果此处忘记调用的话，程序将永远不会停下来。
            channel.close()
        }

        launch {
            // 3: 遍历 Channel 在一个单独的协程里接收管道消息
            for (i in channel) {
                logX("receive: $i")
            }
        }
    }

    /**
     * 测试 Channel 参数 capacity = Channel.UNLIMITED
     *
     * 执行结果：
     * send: 1
     * Thread:main @coroutine#2
     * send: 2
     * Thread:main @coroutine#2
     * send: 3
     * Thread:main @coroutine#2
     * receive: 1
     * Thread:main @coroutine#3
     * receive: 2
     * Thread:main @coroutine#3
     * receive: 3
     * Thread:main @coroutine#3
     *
     * 由于 Channel 的容量是无限大的，所以发送方可以往管道中塞入数据，等待数据塞完之后，接收方才开始接收。
     * 这和之前的交替执行是不一样的。
     *
     */
    fun testChannelCapacity() = runBlocking {
        val channel = Channel<Int>(capacity = Channel.UNLIMITED)
        launch {
            (1..3).forEach {
                // 此处如果设置延时的话，执行顺序则是交替执行的。
//                delay(1000L)
                channel.send(it)
                logX("send: $it")
            }
            channel.close()
        }

        launch {
            for (i in channel) {
                logX("receive: $i")
            }
        }
    }

    /**
     * 测试 Channel 参数 capacity = Channel.CONFLATED
     *
     * 执行结果：
     * send: 1
     * send: 2
     * send: 3
     * receive: 3
     *
     * 只会收到最后一条数据。
     */
    fun testChannelCapacityTwo() = runBlocking {
        val channel = Channel<Int>(capacity = Channel.CONFLATED)
        launch {
            (1..3).forEach {
                channel.send(it)
                println("send: $it")
            }
            channel.close()
        }

        launch {
            for (i in channel) {
                println("receive: $i")
            }
        }
    }

    /**
     * 使用 capacity = 1 和 onBufferOverflow = BufferOverflow.DROP_OLDEST 模拟 capacity = Channel.CONFLATED 情况。
     */
    fun testChannelBuffer() = runBlocking {
        val channel = Channel<Int>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        launch {
            (1..3).forEach {
                channel.send(it)
                println("send: $it")
            }
            channel.close()
        }

        launch {
            for (i in channel) {
                println("receive: $i")
            }
        }
    }

    /**
     * 执行结果：
     * send: 1
     * send: 2
     * send: 3
     * send: 4
     * send: 5
     * receive: 1
     * receive: 2
     * receive: 3
     *
     * 使用 onBufferOverflow = BufferOverflow.DROP_LATEST ，容量满了之后，再继续发送的内容，就会被丢弃
     */
    fun testChannelBufferTwo() = runBlocking {
        val channel = Channel<Int>(capacity = 3, onBufferOverflow = BufferOverflow.DROP_LATEST)
        launch {
            (1..3).forEach {
                channel.send(it)
                println("send: $it")
            }

            channel.send(4)  // 被丢弃
            println("send: 4")
            channel.send(5)  // 被丢弃
            println("send: 5")

            channel.close()
        }

        launch {
            for (i in channel) {
                println("receive: $i")
            }
        }
    }

    /**
     * onUndeliveredElement 回调处理管道中没有被接收的数据
     *
     * 执行结果：
     * onUndeliveredElement: 2
     * onUndeliveredElement: 3
     */
    fun testChannelUndeliveredElement() = runBlocking {
        val channel = Channel<Int>(capacity = Channel.UNLIMITED) {
            // 处理管道中未被接收的数据
            println("onUndeliveredElement: $it")
        }

        (1..3).forEach {
            channel.send(it)
        }

        // 取出一个数据，还剩下两个
        channel.receive()

        // 取消当前 Channel
        channel.cancel()
    }

    /**
     * 使用 produce{} 高阶函数创建 Channel，会自动帮我们调用 close() 方法。
     *
     * 如下代码，我们发送了 3 次数据，却接收了 4 次数据，此时会抛出异常：ClosedReceiveChannelException: Channel was closed。
     * 这也证明了 produce 会帮我们调用 close()，不然的话，第 4 次 receive() 方法会被挂起，而不是抛出异常。
     * 直接调用 receive() 是很容易出问题的，查看正确用法。
     * @see testChannelReceive()
     */
    fun testChannelProduce() = runBlocking {
        val channel = produce {
            (1..3).forEach {
                send(it)
            }
        }

        channel.receive()
        channel.receive()
        channel.receive()
        channel.receive()
    }

    /**
     * Channel 接收数据
     * 任何时候，不要直接使用 receive() 接收数据，很容易出问题。
     *
     * Channel 还有两个属性：
     *      isClosedForSend：对于发送方，我们可以使用 isClosedForSend 判断当前 Channel 是否关闭。
     *      isClosedForReceive：对于接收方，我们可以使用 isClosedForReceive 判断当前 Channel 是否关闭。
     * 使用者两个属性依然不可靠，存在崩溃情况。
     *
     * 因此，我们在读取 Channel 数据时，最好使用 for 循环遍历 Channel 或者使用 Channel.consumeEach {} 高阶函数，
     * 千万不要使用 Channel.receive()。
     * 如果某种场景下，必须我们自己调用 Channel.receive()，可以考虑使用 Channel.receiveCatching()，
     * 防止发生异常。
     */
    fun testChannelReceive() = runBlocking {
        val channel = produce<Int> {
            (1..300).forEach {
                send(it)
            }
        }

        // 此种方式不可靠
//        while (!channel.isClosedForReceive) {
//            println("channel receive: ${channel.receive()}")
//        }

//        while (!channel.isClosedForReceive) {
//            println("channel receive: ${channel.receiveCatching().getOrNull()}")
//        }

        channel.consumeEach {
            println("channel receive: $it")
        }
    }


    /**
     * 为什么说 Channel 是 “热” 的。
     * Channel 其实是用来传递 “数据流” 的。注意，这里的数据流，指的是 多个数据组合形成的流。
     * 前面的挂起函数、async 返回的数据，就像是水滴一样，而 Channel 则像是自来水管中的水流一样。
     *
     * 下面代码执行结果：capacity = 5
     * finish
     * before send: 1
     * send: 1
     * before send: 2
     * send: 2
     * before send: 3
     * send: 3
     *
     * 我们定义了一个 Channel，管道容量为 5，发送了 3 个数据，代码中并没有消费数据。从执行结果可以看出，
     * Channel 不管有没有接收方，发送方都会工作。这种工作模式我们就将其认定为 “热” 的原因。
     *
     * 我们将 capacity = 0 时会怎样呢，Channel 是不是就不会主动工作了呢？
     * * 下面代码执行结果：capacity = 0
     * finish
     * before send: 1
     *
     * 从执行结果可以看出，Channel 依然会主动工作。只是说在它调用 send() 方法时，由于接收方还未就绪，
     * 且管道容量为 0 ，它会被挂起。所以它还是有在工作的。最直接的证据就是：这个程序无法退出，一直运行下去。
     *
     */
    fun testChannelSendData() = runBlocking {
        // 只发送，不接收
        val channel = produce<Int>(capacity = 0) {
            (1..3).forEach {
                println("before send: $it")
                send(it)
                println("send: $it")
            }
        }

        println("finish")
    }
}