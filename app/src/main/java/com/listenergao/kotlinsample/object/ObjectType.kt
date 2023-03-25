package com.listenergao.kotlinsample.`object`

import android.view.View
import com.listenergao.kotlinsample.KotlinApplication


fun main() {


}

/**
 * kotlin 中 object 关键字三种语义，以及它的具体使用场景。
 *
 * 1、匿名内部类
 * 2、单例模式
 * 3、伴生对象
 * 之所以出现这种现象，是因为 kotlin 的设计者认为，这三种语义本质上都是在"声明一个类的同时，还创建了对象"。
 */

/************************************** object 之匿名内部类 start ***********************************/
/**
 * object 之匿名内部类
 */
class ObjectTypeOne {

    /**
     * 这是典型的匿名内部类的写法，因为 View.OnClickListener 是一个接口（抽象类也可以），
     * 我们想要创建它的时候，必须实现它内部没有实现的方法。
     */
    fun testOne() {
        val view = View(KotlinApplication.application)

        // 当 kotlin 的匿名内部类中只有一个方法时，可以使用 SAM 转换，
        // 使用 lambda 表达式简写：view.setOnClickListener {  }
        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                println("ObjectTypeOne  testOne......")
            }

        })
    }

    /**
     * kotlin 的匿名内部类相比 java 来说，还有一个特殊的地方，
     * 就是我们在使用 kotlin 定义匿名内部类时，它可以在继承一个抽象类的同时，来实现多个接口。
     * 如下例
     */
    fun testTwo() {

        // 声明匿名内部类，在继承抽象类 C 的同时，还实现 A、B 接口
        val inner = object : C(), A, B {
            override fun funC() {
                // do something
            }

            override fun funA() {
                // do something
            }

            override fun funB() {
                // do something
            }

        }
    }

    interface A {
        fun funA()
    }

    interface B {
        fun funB()
    }

    abstract class C {
        abstract fun funC()
    }

}
/************************************** object 之匿名内部类 start ***********************************/


/*************************************** object 之单例模式 start ************************************/

/**
 * objet 之单例模式
 *
 * 实现原理：
 *      利用 show kotlin Bytecode 反编译后查看，kotlin 编译器会将其转换为""静态代码块的单例模式，
 *      由于 static 代码块会在类初始化时只加载一次。因此，它在保证线程安全的前提下，同时保证 INSTANCE 实例
 *      仅会初始化一次。
 *
 * 缺点：不支持懒加载、不支持传参构造单例；
 *
 * 反编译后源码：
 *  public final class ObjectTypeTwo {
 *      @NotNull
 *      public static final ObjectTypeTwo INSTANCE;
 *
 *      public final void testTwo() {
 *      }
 *
 *      private ObjectTypeTwo() {
 *      }
 *
 *      static {
 *          ObjectTypeTwo var0 = new ObjectTypeTwo();
 *          INSTANCE = var0;
 *      }
 *  }
 */
object ObjectTypeTwo {

    fun testTwo() {

    }

}

/*************************************** object 之单例模式 end ************************************/


/*************************************** object 之伴生对象 start ***********************************/

/**
 * object 之伴生对象
 * kotlin 中没有 static 关键字，利用伴生对象来实现静态方法和变量。
 */
class ObjectTypeThree {
    /**
     * object 定义单例的一种特殊情况，内部类中定义单例类，单例就与外部类形成类嵌套关系。
     *
     * 具体调用：
     *      kotlin中：ObjectTypeThree.InnerSingleton.foo()
     *      java中  ：ObjectTypeThree.InnerSingleton.INSTANCE.foo()
     *
     * 通过反编译查看：我们发现 foo() 并不是静态方法，而是通过调用单例的实例实现的。
     *
     * 如何实现类似 java 中的静态方法代码呢？
     * 我们只需要使用 "@JvmStatic" 注解 foo() 即可。
     */
    object InnerSingleton {
        @JvmStatic
        fun foo() {

        }
    }

    /**
     * companion object (伴生对象)，它其实是嵌套单例的单例的一种特殊情况。
     * 也就是，在伴生对象的内部，如果存在"@JvmStatic"修饰的方法和属性，它就会被挪到
     * 伴生对象外部的类中，变成静态成员。
     *
     * 查看反编译发现：
     *      被挪到外部的静态方法 other()，它最终还是调用了单例 InnerSingletonOther 的成员方法 other()，
     *      所以它只是做了一层转接而已。
     *
     * 具体调用：(kotlin 与 java 调用一致)
     *      ObjectTypeThree.foo()
     *
     * 由此可见：
     *      object 单例、伴生对象中间的演变关系为：普通的 object 单例，演变出了嵌套的单例；
     *      嵌套的单例，演变出了伴生对象。
     *
     * 或者说：
     *      嵌套单例，是 object 单例的一种特殊情况；伴生对象，是嵌套单例的一种特殊情况。
     */
    companion object InnerSingletonOther {
        @JvmStatic
        fun other() {

        }
    }

}

/*************************************** object 之伴生对象 end ***********************************/