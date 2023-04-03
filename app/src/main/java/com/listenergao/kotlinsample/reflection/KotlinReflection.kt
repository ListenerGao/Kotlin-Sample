package com.listenergao.kotlinsample.reflection

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


fun main() {
    KotlinReflection().apply {
        test()
    }
}

/**
 * kotlin 反射
 *
 * kotlin 反射具备这三个特质：
 *      "感知" 程序的状态，包括程序的运行状态，以及源代码机构；
 *      "修改" 程序的状态；
 *      根据程序的状态，"调整" 自身的决策行为。
 *
 * 在 Kotlin 当中，反射库并没有直接集成到标准库当中。我们如果需要使用的话，需要添加依赖
 * implementation 'org.jetbrains.kotlin:kotlin-reflect:1.x.x'
 *
 * kotlin 反射中关健的 API 和 类：
 *
 * KClass 表示类一个 kotlin 的类：
 *      simpleName：类的名称，对于匿名内部类，则为 null；
 *      qualifiedName：完整的类名；
 *      members：所有成员属性和方法，类型是 Collection<KCallable<*>>；
 *      constructors：类的所有构造函数，类型是 Collection<KFunction<T>>>；
 *      nestedClasses：类的所有嵌套类，类型是 Collection<KClass<*>>>；
 *      visibility：类的可见性，类型是KVisibility?，分别是这几种情况，PUBLIC、PROTECTED、INTERNAL、PRIVATE；
 *      isFinal：是不是 final；
 *      isOpen：是不是 open；
 *      isAbstract：是不是抽象的；
 *      isSealed：是不是密封的；
 *      isData：是不是数据类；
 *      isInner：是不是内部类；
 *      isCompanion：是不是伴生对象；
 *      isFun：是不是函数式接口；
 *      isValue：是不是 Value Class。
 *
 * KCallable 代表了 kotlin 当中的所有可调用的元素，比如函数、属性、甚至是构造函数：
 *      name：名称，这个很好理解，属性和函数都有名称；
 *      parameters：所有的参数，类型是List，指的是调用这个元素所需的所有参数；
 *      returnType：返回值类型，类型是 KType；
 *      typeParameters：所有的类型参数 (比如泛型)，类型是List；
 *      call()：KCallable 对应的调用方法，在前面的例子中，我们就调用过 setter、getter 的 call() 方法。
 *      visibility：可见性；
 *      isSuspend：是不是挂起函数。
 *
 * KParameter 代表了 KCallable 当中的参数：
 *      index：参数的位置，下标从 0 开始；
 *      name：参数的名称，源码当中参数的名称；
 *      type：参数的类型，类型是 KType；
 *      kind：参数的种类，对应三种情况：INSTANCE 是对象实例、EXTENSION_RECEIVER 是扩展接受者、VALUE 是实际的参数值。
 *
 * KType，代表了 Kotlin 当中的类型：
 *      classifier：类型对应的 Kotlin 类，即 KClass，我们前面的例子中，就是用的 classifier == String::class 来判断它是不是 String 类型的；
 *      arguments：类型的类型参数，看起来好像有点绕，其实它就是这个类型的泛型参数；
 *      isMarkedNullable：是否在源代码中标记为可空类型，即这个类型的后面有没有“?”修饰。
 *
 *
 *
 */
class KotlinReflection {


    fun test() {
        val school = School("LX", "ShanDong");
        val student = Student("Jack", 90.5F, 178)

        readMembers(school)
        readMembers(student)
        println()

        modifyAddressMember(school)

        println()
        readMembers(school)
        readMembers(student)

        val java = School::class.java.kotlin
    }

    /**
     * 例如：我们需要读取 obj 对象所有的成员属性名称和值。
     * 此时就需要使用反射了。
     *
     */
    private fun readMembers(obj: Any) {
        obj::class.memberProperties.forEach {
            val className = obj::class.simpleName
            val property = it.name
            val value = it.getter.call(obj)
            println("${className}.$property=$value")
        }
    }

    /**
     * 修改 address 属性的值
     */
    private fun modifyAddressMember(obj: Any) {

        obj::class.memberProperties.forEach {
            if (it.name == "address" &&                             // 判断属性名称
                it is KMutableProperty1 &&                          // 判断属性是否可变，address 是 var 修饰的，因此它的类型是 KMutableProperty1
                it.setter.parameters.size == 2 &&                   // 我们在后面要调用属性的 setter，所以我们要先判断 setter 的参数是否符合预期，这里 setter 的参数个数应该是 2，第一个参数是 obj 自身，第二个是实际的值
                it.getter.returnType.classifier == String::class    // 根据属性的 getter 的返回值类型 returnType，来判断属性的类型是不是 String 类型
            ) {
                // 调用属性的 setter 方法，传入 obj，还有“China”，来完成属性的赋值。
                it.setter.call(obj, "China")
                println("address changed...")
            }

            println(
                """
                is:${it.isAccessible}
            """.trimIndent()
            )
        }
    }
}

data class School(
    val name: String,
    var address: String
)

data class Student(
    var name: String,
    var score: Float,
    var height: Int
)