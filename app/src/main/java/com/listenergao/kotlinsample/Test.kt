package com.listenergao.kotlinsample


fun main() {
    println("LG".lastElement())
}

fun String.lastElement():Char?{
    if (this.isEmpty()) {
        return null
    }
    return this[lastIndex]
}