package com.listenergao.kotlinsample.function

import android.view.View
import android.view.ViewGroup
import com.listenergao.kotlinsample.KotlinApplication

fun main() {


    println("LG".lastElement())
    println("LG".firstElement)

}

/**
 * kotlin 扩展：扩展函数和扩展属性
 *
 * 扩展函数：
 *      就是在类的外部扩展出来一个函数，这个函数看起来像是类的成员函数一样。
 * 扩展属性：
 *      就是在类的外部定义一个新的成员属性。
 *
 * 在 Kotlin 当中，几乎所有的类都可以被扩展，包括普通类、单例类、密封类、枚举类、伴生对象，甚至还包括第三方提供的 Java 类。
 * 唯有匿名内部类，由于它本身不存在名称，我们无法指定“接收者类型”，所以不能被扩展，当然了，它也没必要被扩展。
 *
 * 可以说，Kotlin 扩展的应用范围还是非常广的。它最主要的用途，就是用来取代 Java 当中的各种工具类，比如 StringUtils、DateUtils 等等。
 * 所有 Java 工具类能做的事情，Kotlin 扩展函数都可以做，并且可以做得更好。扩展函数的优势在于，开发工具可以在编写代码的时候智能提示。
 *
 * 总结：
 *      1、Kotlin 的扩展，从 "语法角度" 来看，分为扩展函数和扩展属性。定义扩展的方式，只是比普通函数、属性多了一个“扩展接收者”而已。
 *      2、从作用域角度来看，分为顶层扩展和类内扩展。
 *      3、从本质上来看，扩展函数和扩展属性，它们都是 Java 静态方法，与 Java 当中的工具类别无二致。对比 Java 工具类，扩展最大的优势就在于，IDE 可以为我们提供代码补全功能。
 *      4、从能力的角度来看，Kotlin 扩展一共有三个限制，分别是：扩展无法被重写；扩展属性无法存储状态；扩展的作用域有限，无法访问私有成员。
 *      5、从使用场景的角度来看，Kotlin 扩展主要有两个使用场景，分别是：关注点分离，优化代码架构；消灭模板代码，提高可读性和开发效率。
 */
class KotlinExtension {


    private fun test() {
        val view = View(KotlinApplication.application)
        view.updateMargin(left = 100, right = 100)
    }


}


/**
 * 扩展函数
 *
 * 以 JDK 中的 String 为例，利用 kotlin 扩展的特性，为它定义一个 lastElement() 方法
 * 这个扩展函数是直接定义在 kotlin 文件里面的，而不是定义在某个类中。这种扩展函数，我们称之为 "顶层函数"。
 *      ①：fun 关键字，代表定义一个函数。也就是说，不管定义普通函数，还是扩展函数，都需要 fun 关键字。
 *      ②："String" 代表我们这个函数是为 String 这个类定义的。在 kotlin 中，它叫做 "接收者"，也就是扩展函数的接收方。
 *      ③：lastElement()，这个是我们定义的扩展函数的名称。
 *      ④：Char? 代表扩展函数的返回值为 可空的 Char 类型
 *      ⑤：this 代表 "具体的 String 对象"。在整个扩展函数的方法体当中，this 都是可以省略的。
 *
 * 我们在普通函数的名称前面，添加一个 "接收者类型"，比如 "String." ，kotlin 的 "普通函数" 就变成了 "扩展函数"。
 *
 * public final class KotlinExtensionKt {
 *      public static final void main() {
 *          Character var0 = lastElement("LG");
 *          System.out.println(var0);
 *      }
 *
 *      // $FF: synthetic method
 *      public static void main(String[] var0) {
 *          main();
 *      }
 *
 *      @Nullable
 *      public static final Character lastElement(@NotNull String $this$lastElement) {
 *          Intrinsics.checkNotNullParameter($this$lastElement, "$this$lastElement");
 *          CharSequence var1 = (CharSequence)$this$lastElement;
 *          return var1.length() == 0 ? null : $this$lastElement.charAt(StringsKt.getLastIndex((CharSequence)$this$lastElement));
 *      }
 * }
 * 通过反编译后发现，lastElement() 变成了一个普通的静态方法，并且多了一个 String 类型的参数：lastElement(String $this$lastElement)，
 * 原本 "LG".lastElement() 调用方法变成了 lastElement("LG")。这说明，kotlin 编写的扩展函数调用代码，最终会变成静态方法的调用。
 * Kotlin 的扩展函数只是从表面上将 lastElement() 变成 String 的成员，但它实际上并没有修改 String 这个类的源代码，
 * lastElement() 也并没有真正变成 String 的成员方法。
 * 也就是说，由于 JVM 不理解 Kotlin 的扩展语法，所以 Kotlin 编译器会将扩展函数转换成对应的静态方法，
 * 而扩展函数调用处的代码也会被转换成静态方法的调用。
 *
 */
/*
 ①    ②         ③         ④
 ↓     ↓         ↓          ↓                */
fun String.lastElement(): Char? {
    //   ⑤
    //   ↓
    if (this.isEmpty()) {
        return null
    }
    return this[lastIndex]
}

/**
 * 扩展属性：
 *
 * 反编译后几乎和前边定义的扩展函数一摸一样。
 *
 * Kotlin 的扩展表面上看起来是为一个类扩展了新的成员，但是本质上，它还是静态方法。
 * 而且，不管是扩展函数还是扩展属性，它本质上都会变成一个静态的方法。
 *
 * 那么，什么时候使用扩展函数，什么时候使用扩展属性呢？
 * 我们只需要看扩展在语义上更适合作为函数还是属性就够了。比如这里的 lastElement，它更适合作为一个扩展属性。
 * 这样设计的话，在语义上，lastElement 就像是 String 类当中的属性一样，它代表了字符串里的最后一个字符。
 *
 */

// 接收者类型
//     ↓
val String.firstElement: Char?
    get() = if (this.isEmpty()) {
        null
    } else {
        this[0]
    }


inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(block: T.() -> Unit) {
    val newLayoutParams = layoutParams as T
    block(newLayoutParams)
    layoutParams = newLayoutParams
}

fun View.updateMargin(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null,
) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            left?.let {
                marginStart = left
            }

            top?.let {
                topMargin = top
            }

            right?.let {
                marginEnd = right
            }

            bottom?.let {
                bottomMargin = bottom
            }
        }
    }
}
