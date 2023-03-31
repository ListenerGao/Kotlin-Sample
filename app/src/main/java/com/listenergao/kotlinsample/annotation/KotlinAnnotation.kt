package com.listenergao.kotlinsample.annotation

import com.listenergao.kotlinsample.actualProject.calculator.CalculatorV2


fun main() {

    val kotlinAnnotation = KotlinAnnotation()

    val calculatorV2 = CalculatorV2()

}

/**
 * kotlin Annotation (注解)
 *
 * Kotlin 的泛型，就是在代码架构的层面进行的一种抽象，从而达到代码逻辑尽可能复用的目的。
 * 那么，注解与反射，它们存在的意义是什么呢？答案是：提高代码的灵活性。
 *
 * 有点像我们生活中的 "便利贴"，比如学习看书的时候，对书中的某个知识点有了新的感悟，就会用便利贴写下当时的想法，贴到书本上。
 * 还有就是我们在看维基百科时，也会看到 "注解"，它就是 "对已有数据进行补充的一种数据"。
 * 学习的时候，写下便利贴注解，其实就是对知识点的补充；
 * 维基百科中的注解，其实就是对它描述内容的一种补充；
 *
 * kotlin 中的注解，其实就是 "程序代码上的一种补充"
 */

class KotlinAnnotation {

    fun test() {

    }

}


/**
 * 自定义 Deprecated 注解。
 *
 * 我们可以看到 @Deprecated 这个注解定义上面，还有其它注解 @Target、@MustBeDocumented。
 * 这样的注解我们称之为 "元注解"。
 *
 * kotlin 常见的元注解有四个：
 *      @Target :指定被修饰的注解可以用在什么地方，也就是目标；
 *      @Retention :指定被修饰的注解的状态，编译后可见还是运行时可见；
 *      @Repeatable :这个注解允许我们在同一个地方，多次使用相同的被修饰的注解，使用场景较少；
 *      @MustBeDocumented :指定被修饰的注解应该包含在生成的 API 文档中显示，这个注解一般用于 SDK 当中。
 *
 * 元注解 @Target 取值：（可以查看 AnnotationTarget 枚举类）
 *          CLASS：                  类、接口、object、注解类,
 *          ANNOTATION_CLASS：       注解类,
 *          TYPE_PARAMETER：         泛型参数,
 *          PROPERTY：               属性,
 *          FIELD：                  字段、幕后字段,
 *          LOCAL_VARIABLE：         局部变量,
 *          VALUE_PARAMETER：        函数参数,
 *          CONSTRUCTOR：            构造器,
 *          FUNCTION：               函数,
 *          PROPERTY_GETTER：        属性的getter,
 *          PROPERTY_SETTER：        属性的setter,
 *          TYPE：                   类型,
 *          EXPRESSION：             表达式,
 *          FILE：                   文件,
 *          TYPEALIAS：              类型别名
 *
 * 元注解 @Retention 取值：（可以查看 AnnotationRetention 枚举类）
 *          SOURCE：              注解只存在于源代码，编译后不可见，
 *          BINARY：              注解编译后可见，运行时不可见，
 *          RUNTIME：             注解编译后可见，运行时可见
 *
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Deprecated(
    val message: String,                                            // 代表了废弃的提示信息。
    val replaceWith: ReplaceWith = ReplaceWith(""),       // 代表了应该用什么来替代废弃部分。
    val level: DeprecationLevel = DeprecationLevel.WARNING          // 代表了警告的程度，分别是 WARNING、ERROR、HIDDEN。
)