package com.listenergao.kotlinsample.function

import android.view.View
import android.widget.TextView
import com.listenergao.kotlinsample.android.KotlinApplication
import com.listenergao.kotlinsample.reflection.Student


fun main() {

    val kotlinHeightLevelFunction = KotlinHeightLevelFunction()


}

/**
 *
 * 理解高阶函数的相关概念：
 *      函数类型：顾名思义，函数类型就是函数的类型。例如下面的函数：
 *                     (Int,  Int) -> Float  这就是 add 函数的类型
 *                       ↑      ↑      ↑
 *          fun add(a: Int, b: Int): Float { return (a + b).toFloat() }
 *          将函数的 "参数类型" 和 "返回值类型" 抽象出来，就得到了 "函数类型"。
 *          (Int, Int) -> Float 就代表了参数类型是两个 Int，返回值类型是 Float。
 *
 *      函数的引用：普通变量也有引用的概念，我们将变量赋值给另一个变量，而这一点，在函数上也是同样适用的，
 *      函数也有引用，并且也可以赋值给变量。如下：
 *
 *             函数赋值给变量                    函数引用
 *                ↑                              ↑
 *          val function: (Int, Int) -> Float = ::add
 *
 * 高阶函数：高阶函数是将函数作为参数或者返回值的函数。换句话说，一个函数的参数或者返回值，它们当中有一个是函数的情况下，
 * 这个函数就是高阶函数。如 ① 代码
 *
 * Lambda 表达式：
 *      kotlin 语言的设计者是用 Lambda 表达式作为函数的参数，那么这里的 Lambda 就可以理解为函数的简写：
 *
 *      fun onClick(v: View): Unit { ... }
 *      setOnClickListener(::onClick)
 *
 *      用 Lambda 表达式来替代函数引用
 *      setOnClickListener({v: View -> ...})
 *
 *      Android 并没有提供 View.java 的 Kotlin 实现，那么为什么我们为什么可以用 Lambda 来简化事件监听呢？
 *      由于 OnClickListener 符合 SAM 转换的要求，因此编译器自动帮我们做了一层转换，
 *      让我们可以用 Lambda 表达式来简化我们的函数调用。
 *
 * SAM 转换：
 *      SAM 是 Single Abstract Method 的缩写，意思就是只有一个抽象方法的类或者接口。
 *      但在 Kotlin 和 Java 8 里，SAM 代表着只有一个抽象方法的接口。只要是符合 SAM 要求的接口，
 *      编译器就能进行 SAM 转换，也就是我们可以使用 Lambda 表达式，来简写接口类的参数。
 *
 *      注意，Java 8 中的 SAM 有明确的名称，叫做函数式接口（FunctionalInterface）。
 *      FunctionalInterface 的限制如下，缺一不可：
 *          A：必须是接口，抽象类不行；
 *          B：该接口有且仅有一个抽象的方法，抽象方法个数必须是 1，默认实现的方法可以有多个。
 *
 * Lambda 表达式 8 种写法。如 ② 代码
 *
 * 带接收者的函数类型。如 ③ 代码
 *
 * kotlin 引入全新的高阶函数，最终变成 JVM 字节码后是怎么执行的？
 * 是以 "匿名内部类" 来执行的。
 *
 */
class KotlinHeightLevelFunction {

    private fun add(a: Int, b: Int): Float {
        return (a + b).toFloat()
    }

    val function: (Int, Int) -> Float = ::add

    // ①                     函数作为参数的高阶函数
    //                                 ↓
    fun setOnClickListener(listener: (View) -> Unit) {

    }

    // ②
    private val imageView = View(KotlinApplication.application)
    fun testLambda() {

        /**
         * 第 1 中写法
         *
         * 原始代码，它的本质是用 object 关键字定义了一个匿名内部类
         */
        imageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                println("onClick...")
            }
        })

        /**
         * 第 2 中写法
         *
         * object 关键字可以省略。这时候在语法层面就不再是匿名内部类了，它更像是 Lambda 表达式，
         * 因为它里面 override 的方法也跟着被省略。
         * View.OnClickListener{} 被称为 SAM Constructor (SAM 构造器)，它是在编译期为我们生成的。
         */
        imageView.setOnClickListener(View.OnClickListener {
            println("onClick...")
        })

        /**
         * 第 3 种写法
         *
         * 由于 kotlin 的 Lambda 表达式是不需要 SAM Constructor 的，所以它可以省略。
         */
        imageView.setOnClickListener({ view: View? ->
            println("onClick...")
        })

        /**
         * 第 4 种写法
         *
         * 由于 kotlin 支持类型推导，所以类型 View 也可以横掠
         */
        imageView.setOnClickListener({ view ->
            println("onClick...")
        })

        /**
         * 第 5 种写法
         *
         * 当 Kotlin Lambda 表达式只有一个参数的时候，它可以被写成 it
         */
        imageView.setOnClickListener({ it ->
            println("onClick...")
        })

        /**
         * 第 6 种写法
         *
         * Kotlin Lambda 的 it 是可以被省略的
         */
        imageView.setOnClickListener({
            println("onClick...")
        })

        /**
         * 第 7 种写法
         *
         * 当 Kotlin Lambda 作为函数的最后一个参数时，Lambda 可以被挪到外面
         */
        imageView.setOnClickListener() {
            println("onClick...")
        }

        /**
         * 第 8 种写法
         *
         * 当 Kotlin 只有一个 Lambda 作为函数参数时，() 可以被省略
         */
        imageView.setOnClickListener {
            println("onClick...")
        }

    }

    /**
     * ③
     * 带接收者的函数类型.
     * 我们看一下 Kotlin 的标准函数 apply 的使用场景。
     */
    private fun testFunction(student: Student?) {

        val stuName = TextView(KotlinApplication.application)
        val stuHeight = TextView(KotlinApplication.application)
        val stuScore = TextView(KotlinApplication.application)

        // 不使用 apply
        if (student != null) {
            stuName.text = student.name
            stuHeight.text = student.height.toString()
            stuScore.text = student.score.toString()
        }

        /**
         * 使用 apply
         *
         * 我们反推一下 apply 方法的实现
         *
         * apply 肯定是个函数，所以有 () ,只是被省略了
         * student?.apply() {...}
         *
         * Lambda 肯定是在 () 里面
         * student?.apply ({...})
         *
         * 由于 gotoImagePreviewActivity(this) 里面，this 代表了 student，
         * 所以 student 应该是 apply 函数的一个参数，而且参数名为：this
         * student?.apply ({ this: Student -> ... })
         *
         * 所以，apply 其实是接收了一个 Lambda 表达式：{ this: Student -> ... }，我们尝试实现一下：
         *
         *      // 函数形参不允许命名为 this，因此我们这里用 self。
         *      fun Student.apply(self: Student, block: (Student) -> Unit): Student {
         *              block.invoke(self)
         *              return this
         *      }
         *      student?.apply(self = student) { self: Student ->
         *              // 我们反推出来的 apply，需要使用 self.name 的方式来访问变量。
         *              stuName.text = self.name
         *              stuHeight.text = self.height.toString()
         *              stuScore.text = self.score.toString()
         *              imageView.setOnClickListener {
         *                  gotoImagePreviewActivity(self)
         *              }
         *      }
         * 从上面例子可以看出，反推出的 apply 实现会比较繁琐：
         *      1：需要我们传入 this：student?.apply(this = student).
         *      2：需要我们自己调用：block(this)
         * 因此 kotlin 引入了 "带接收者的函数类型" 可以简化 apply 定义。如下代码：
         *      //                    带接收者函数类型
         *      //                          ↓
         *      fun Student.apply(block: Student.() -> Unit): Student {
         *              // 不用传 this
         *              block()
         *              return this
         *      }
         *
         *      student?.apply {
         *              stuName.text = name // this 可以省略
         *              stuHeight.text = height.toString()
         *              stuScore.text = score.toString()
         *              imageView.setOnClickListener {
         *                      gotoImagePreviewActivity(this)
         *              }
         *      }
         * 上面的 apply 方法是不是看起来像是在 Student 类里，增加了一个成员方法 apply() ?
         * 所以，从外表上看，带接收者的函数类型，就等价于成员方法。但从本质上讲，它仍然是通过编译器注入 this 来实现的。
         *
         */
        student?.apply {
            stuName.text = this.name // this 可以省略
            stuHeight.text = height.toString()
            stuScore.text = score.toString()
            imageView.setOnClickListener {
                gotoImagePreviewActivity(this)
            }
        }


    }

    private fun gotoImagePreviewActivity(student: Student) {

    }
}