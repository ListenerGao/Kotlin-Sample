package com.listenergao.kotlinsample.actualProject.calculator

import kotlin.system.exitProcess


fun main() {

    val calculatorV2 = CalculatorV2()
    calculatorV2.start()

}

@com.listenergao.kotlinsample.annotation.Deprecated(
    message = "Use CalculatorV3 instead",
    replaceWith = ReplaceWith("CalculatorV3"),
    level = DeprecationLevel.ERROR
)
class CalculatorV2 {

    companion object {
        private val help = """
--------------------------------------
使用说明：
1. 输入 1 + 1，按回车，即可使用计算器；
2. 注意：数字与符号之间要有空格；
3. 想要退出程序，请输入：exit
--------------------------------------
""".trimIndent()

        private const val EXIT = "exit"
    }


    fun start() {
        while (true) {
            println(help)

            val input = readLine() ?: continue
            val result = calculate(input)
            if (result == null) {
                println("输入格式有误\n")
            } else {
                println("input=$result")
            }
        }
    }

    private fun calculate(input: String): String? {
        if (shouldExit(input)) {
            exitProcess(0)
        }

        val exp = parseExpression(input) ?: return null
        val left = exp.left
        val operation = exp.operation
        val right = exp.right

        return when (operation) {
            Operation.ADD -> addString(left, right)
            Operation.MINUS -> minusString(left, right)
            Operation.MULTI -> multiString(left, right)
            Operation.DIVI -> diviString(left, right)
        }

    }

    private fun addString(left: String, right: String): String {
        val result = left.toInt() + right.toInt()
        return result.toString()
    }

    private fun minusString(left: String, right: String): String {
        val result = left.toInt() - right.toInt()
        return result.toString()
    }

    private fun multiString(left: String, right: String): String {
        val result = left.toInt() * right.toInt()
        return result.toString()
    }

    private fun diviString(left: String, right: String): String {
        val result = left.toInt() / right.toInt()
        return result.toString()
    }

    private fun parseExpression(input: String): Expression? {
        val operation = parseOperator(input) ?: return null
        val strings = input.split(operation.value)
        if (strings.size != 2) return null
        return Expression(
            left = strings[0].trim(),
            operation = operation,
            right = strings[1].trim()
        )
    }

    private fun parseOperator(input: String): Operation? {
        Operation.values().forEach {
            if (input.contains(it.value)) {
                return it
            }
        }
        return null
    }

    private fun shouldExit(input: String) = input == EXIT
}

data class Expression(
    val left: String,
    val operation: Operation,
    val right: String
)