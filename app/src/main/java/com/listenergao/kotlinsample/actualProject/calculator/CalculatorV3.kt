package com.listenergao.kotlinsample.actualProject.calculator

import kotlin.system.exitProcess


fun main() {

    val calculatorV3 = CalculatorV3()
    calculatorV3.start()

}

class CalculatorV3 {

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

    fun calculate(input: String): String? {
        if (shouldExit(input)) {
            exitProcess(0)
        }

//        val exp = parseExpression(input) ?: return null
//        val left = exp.left
//        val operation = exp.operation
//        val right = exp.right

        val (left, operation, right) = parseExpression(input) ?: return null

        return when (operation) {
            Operation.ADD -> addString(left, right)
            Operation.MINUS -> minusString(left, right)
            Operation.MULTI -> multiString(left, right)
            Operation.DIVI -> diviString(left, right)
        }

    }

    private fun addString(left: String, right: String): String {

        val result = StringBuilder();

        var leftIndex = left.length - 1
        var rightIndex = right.length - 1;

        // 计算结果的进位
        var carry = 0

        while (leftIndex >= 0 || rightIndex >= 0) {

            val leftVal = if (leftIndex >= 0) left[leftIndex].digitToInt() else 0
            val rightVal = if (rightIndex >= 0) right[rightIndex].digitToInt() else 0

            val sum = leftVal + rightVal + carry
            carry = sum / 10
            result.append(sum % 10)

            leftIndex--
            rightIndex--
        }
        // 兼容 99+1=00 问题
        if (carry != 0) {
            result.append(carry)
        }

        return result.reverse().toString()
    }

    private fun minusString(left: String, right: String): String {
        val result = StringBuilder()
        var leftIndex = left.length - 1
        var rightIndex = right.length - 1
        // 判断结果是否是负数
        val isNegativeNumber =
            leftIndex < rightIndex || leftIndex == rightIndex && left[0] < right[0]

        // 是否借位
        var borrow = 0

        while (leftIndex >= 0 || rightIndex >= 0) {

            var leftVal = if (leftIndex >= 0) left[leftIndex].digitToInt() else 0
            val rightVal = if (rightIndex >= 0) right[rightIndex].digitToInt() else 0

            leftVal -= borrow

            borrow = if (leftVal >= rightVal) {
                0
            } else {
                1
            }

            val minus = borrow * 10 + leftVal - rightVal


            result.append(minus)

            leftIndex--
            rightIndex--
        }

        if (result[result.lastIndex] == '0') {
            result.deleteAt(result.lastIndex)
        }


        return result.reverse().toString()
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
