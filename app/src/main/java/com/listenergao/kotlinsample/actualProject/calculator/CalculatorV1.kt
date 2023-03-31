package com.listenergao.kotlinsample.actualProject.calculator

import kotlin.system.exitProcess

val help = """
--------------------------------------
使用说明：
1. 输入 1 + 1，按回车，即可使用计算器；
2. 注意：数字与符号之间要有空格；
3. 想要退出程序，请输入：exit
--------------------------------------
""".trimIndent()

fun main() {

    // 初始化，打印提示信息；
    // 第一步，读取输入命令；
    // 第二步，判断命令是不是 exit，如果用户输入的是“exit”则直接退出程序；
    // 第三步，解析算式，分解出“数字”与“操作符”：“1”“+”“2”；
    // 第四步，根据操作符类型，算出结果：3；第五步，输出结果：1 + 2 = 3；
    // 第六步，进入下一个 while 循环。


    while (true) {
        // 初始化，打印提示信息
        println(help)

        // 第一步，读取输入的命令
        val input = readLine() ?: continue
        // 第二步，判断是否是退出指令
        if (input == "exit") exitProcess(0)
        // 第三步，解析算式
        val split = input.split(" ")
        // 第四步，算出结果
        val result = calculate(split)
        if (result == null) {
            println("输入格式有误")
            continue
        } else {
            println("result:$result")
        }
    }
}

fun calculate(stringList: List<String>): Int? {
    if (stringList.size < 3) {
        return null
    }
    val left = stringList[0].toInt()
    val operation = Operation.valueOf(stringList[1])
    val right = stringList[2].toInt()


    return when (operation) {
        Operation.ADD -> left + right
        Operation.MINUS -> left - right
        Operation.MULTI -> left * right
        Operation.DIVI -> left / right

    }
}

enum class Operation(val value: String) {
    ADD("+"),
    MINUS("-"),
    MULTI("*"),
    DIVI("/")
}
