package com.listenergao.kotlinsample.`object`.apply

/**
 * 抽象类模版单例对象
 * P：表示 getInstance 传入参数类型
 * T：表示 getInstance 返回值类型
 */
abstract class BaseSingleton<in P, out T> {

    @Volatile
    private var instance: T? = null

    abstract fun creator(param: P): T

    fun getInstance(param: P): T =
        instance ?: synchronized(this) {
            instance ?: creator(param).also {
                instance = it
            }

        }
}