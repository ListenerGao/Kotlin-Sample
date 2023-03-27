package com.listenergao.kotlinsample.`object`.apply


/**
 * 工厂模式：当我们想要统一管理一个类的创建时，我们可以将这个类的构造方法声明成私有的，
 * 然后使用工厂模式来暴露一个统一的方法，供外部使用。
 * companion object 就非常符合这样的使用场景。
 */
class User private constructor(name: String) {
    companion object {
        @JvmStatic
        fun create(name: String): User {
            // 统一检查，比如敏感词过滤
            return User(name)
        }
    }
}