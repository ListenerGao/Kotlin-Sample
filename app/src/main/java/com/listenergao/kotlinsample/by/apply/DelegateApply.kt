package com.listenergao.kotlinsample.by.apply


fun main() {

}

/**
 * kotlin 委托案例
 */
class DelegateApply {


    private fun test() {
        val model = Model()

    }
}

/**
 * 案例1：属性可见性封装
 *
 * 对于某个成员变量 data，我们希望外部类可以访问它的值，但不允许外部修改它的值，我们经常写出如下类似的代码。
 *
 * 如何 data 的类型由 String 变成集合后，问题就不一样类。如果我们还用 private set 设置的话，
 * 就不能达到上面所说效果，外部依然能向集合中添加或删除值。
 */
class Model {
    var data: String = ""
        private set

    var list: MutableList<String> = mutableListOf()
        private set

    fun load() {
        data = "网络结果"
    }

    /******************************************************/

    /**
     * 使用 委托属性 就能很好的解决这个问题。
     * kotlin 中的 List 是不可修改的，不支持 add，remove。
     */
    val newData: List<String> by ::_newData
    private var _newData: MutableList<String> = mutableListOf()

    fun loadNewData() {
        _newData.add("ListenerGao")
    }

}