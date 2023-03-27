package com.listenergao.kotlinsample.`object`.apply

/**
 * 接口模版单例类
 * 利用 kotlin 接口的两个特性：接口可以有属性、接口方法可以有默认实现。
 *
 * 不推荐：
 *     1、instance 无法使用 private 修饰
 *     2、instance 无法使用 @Volatile 修饰
 *     不符合单例模式，存在缺陷。
 */
interface ISingleton<P, T> {

    var instance: T?

    fun creator(param: P): T

    fun getInstance(param: P): T =
        instance ?: synchronized(this) {
            instance ?: creator(param).also {
                instance = it
            }
        }

}