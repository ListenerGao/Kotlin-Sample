package com.listenergao.kotlinsample.`object`.apply

/**
 * kotlin 中的单例模式
 *
 * 之前我们使用 object 创建单例，它存在两个问题：1、无法懒加载；2、无法传参；
 */


/**
 * 一、懒加载委托单例模式
 */
object UserManager {

    // 对外暴露 user
    // 懒加载，只要外部没有使用过 user 变量，就不会触发 loadUser() 的逻辑。
    val user: User by lazy {
        loadUser()
    }

    private fun loadUser(): User {
        return User.create("ListenerGao")
    }

}


/**
 * 二、Double Check 单例模式
 */
class Singleton private constructor(private val name: String) {

    companion object {

        @Volatile
        private var INSTANCE: Singleton? = null

        @JvmStatic
        fun getInstance(name: String): Singleton =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Singleton(name).also {
                    INSTANCE = it
                }
            }
    }
}

/**
 * 三、抽象模版实现单例
 */
class SampleSingleton private constructor(private val name: String) {

    companion object : BaseSingleton<String, SampleSingleton>() {
        override fun creator(param: String): SampleSingleton = SampleSingleton(param)

    }
}

/**
 * 四、接口模版实现单例
 */

class SingletonImpl private constructor(private val name: String) {
    companion object : ISingleton<String, SingletonImpl> {
        @Volatile
        override var instance: SingletonImpl? = null

        override fun creator(param: String): SingletonImpl = SingletonImpl(param)

    }
}