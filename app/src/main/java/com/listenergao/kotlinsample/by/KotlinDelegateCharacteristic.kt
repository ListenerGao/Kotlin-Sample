package com.listenergao.kotlinsample.by

import kotlin.reflect.KProperty


fun main() {

    KotlinDelegateCharacteristic().apply {
//        testDelegateClass()
    }

    LazyDelegate().apply {
        test()
    }

}

/**
 * kotlin 委托特性
 *
 * 委托类
 * 委托属性
 * 懒加载委托
 */
class KotlinDelegateCharacteristic {

    /**
     * 委托类
     * Kotlin 的委托类提供了语法层面的委托模式。
     * 通过这个 by 关键字，就可以自动将接口里的方法委托给一个对象，
     * 从而可以帮我们省略很多接口方法适配的模板代码。
     */
    fun testDelegateClass() {
        UniversalDB(SqlDB()).save()
        UniversalDB(GreenDaoDB()).save()
    }

}

/****************************************** 委托类 start ******************************************/

/**
 * 委托类
 *
 * Kotlin "委托类" 委托的是接口方法；
 */

interface DB {
    fun save()
}

class SqlDB : DB {
    override fun save() {
        println("save to sql")
    }
}

class GreenDaoDB : DB {
    override fun save() {
        println("save to GreenDao")
    }
}

/**
 *  通过 by 将接口实现委托给了它的参数 db
 */
class UniversalDB(db: DB) : DB by db

/********************************************* 委托类 end ******************************************/

/********************************************* 委托属性 start **************************************/

/**
 * 委托属性
 *
 * kotlin "委托属性" 委托的是属性的 setter 和 getter
 */
class BaseResponse {
    /**
     * 将 total 属性的 setter 和 getter 委托给 count；
     * 两者之间的委托关系一旦建立，就表示两者的 setter 和 getter 会完全绑定在一起。
     */
    var count: Int = 0

    var total: Int by ::count
}

/********************************************* 委托属性 end ****************************************/

/******************************************* 懒加载委托 start **************************************/

class LazyDelegate {

    private val data: String by lazy {
        request()
    }

    private fun request(): String {
        println("执行网络请求")
        return "网络数据"
    }

    /**
     * 结果：
     * 开始
     * 执行网络请求
     * 网络数据
     * 网络数据
     *
     * 通过 by lazy{} 我们就可以实现属性的懒加载了。
     * 根据输入结果可知，当 data 有值时，不会重复计算，就直接返回结果了。
     */
    fun test() {
        println("开始")
        println(data)
        println(data)
    }
}

/******************************************** 懒加载委托 end ***************************************/


/******************************************** 自定义委托 start *************************************/


class StringDelegate(private var s: String = "Hello") {
    //     ①                           ②                              ③
//     ↓                            ↓                               ↓
    operator fun getValue(thisRef: Owner, property: KProperty<*>): String {
        return s
    }

    //      ①                          ②                                     ③
//      ↓                           ↓                                      ↓
    operator fun setValue(thisRef: Owner, property: KProperty<*>, value: String) {
        s = value
    }
}

//      ②
//      ↓
class Owner {
    //               ③
//               ↓
    var text: String by StringDelegate()
}

/******************************************** 自定义委托 end ***************************************/




