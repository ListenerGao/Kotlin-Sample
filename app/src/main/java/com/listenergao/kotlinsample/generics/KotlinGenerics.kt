package com.listenergao.kotlinsample.generics


fun main() {

    KotlinGenericsTwo().apply {
        test()
    }

}

class KotlinGenerics() {

    class Control<T> {
        fun turnOn(tv: T) {

        }

        fun turnOff(tv: T) {

        }
    }
}


/**
 * kotlin 泛型
 *
 * 协变：父子关系一致，子类可以作为参数传进来。 集合中，只能读，不能写。
 * 逆变：父子关系颠倒，负类可以作为参数传进来。 集合中，只能写，不能读。
 *
 *      语言类型                协变                            逆变
 *       java        ArrayList<? extends Fruit>       ArrayList<? super Apple>
 *       kotlin      ArrayList<out Fruit>             ArrayList<in Apple>
 */
class KotlinGenericsTwo {

    private val apples = arrayListOf(
        Apple(0.9F),
        Apple(0.9F),
        Apple(0.9F),
        Apple(0.9F),
        Apple(0.9F),
    )

    private val bananas = arrayListOf(
        Banana(0.5F),
        Banana(0.5F),
        Banana(0.5F),
        Banana(0.5F),
        Banana(0.5F),
    )


    private fun getFruitWeight(fruit: ArrayList<out Fruit>): Float {
        var fruitWeight = 0F
        fruit.forEach {
            fruitWeight += it.getWeight()
        }
        return fruitWeight
    }

    fun test() {
        println("appleWeight:${getFruitWeight(apples)}")
        println("bananaWeight:${getFruitWeight(bananas)}")

//        val list:ArrayList<out Fruit> = ArrayList<Apple>()
    }

    interface Fruit {
        fun getWeight(): Float
    }


    class Apple(private val weight: Float) : Fruit {
        override fun getWeight(): Float {
            return weight
        }
    }

    class Banana(private val weight: Float) : Fruit {
        override fun getWeight(): Float {
            return weight
        }
    }

}


