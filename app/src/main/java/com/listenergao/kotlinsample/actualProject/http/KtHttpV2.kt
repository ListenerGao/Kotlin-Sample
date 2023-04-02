package com.listenergao.kotlinsample.actualProject.http

import com.google.gson.Gson
import com.listenergao.kotlinsample.actualProject.http.annoatation.Field
import com.listenergao.kotlinsample.actualProject.http.annoatation.GET
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Method
import java.lang.reflect.Proxy

fun main() {
    KtHttpV2.baseUrl = "https://www.wanandroid.com"

    val apiService = KtHttpV2.create<ApiService>()
    val articleInfo = apiService.getArticle(cid = 60)
    println(articleInfo)
}

object KtHttpV2 {
    private val okHttpClient by lazy { OkHttpClient() }
    private val gson by lazy { Gson() }

    var baseUrl = ""


    // 使用 inline + reified 实现真泛型
    inline fun <reified T> create(): T {
        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java)
        ) { proxy, method, args ->

            if (baseUrl.isEmpty()) {
                throw NullPointerException("Host not set...")
            }

            return@newProxyInstance method.annotations
                .filterIsInstance<GET>()
                .takeIf { it.size == 1 }
                ?.let {
                    invoke("$baseUrl${it[0].value}", method, args)
                }
        } as T
    }

    fun invoke(url: String, method: Method, args: Array<Any>): Any? =
        method.parameterAnnotations
            // 判断
            .takeIf { it.size == args.size }
            // map 升级版，多了一个 index
            ?.mapIndexed { index, annotation -> Pair(annotation, args[index]) }
            // 相当于高阶函数中的 for 循环
            ?.fold(url, ::parseUrl)
            ?.let {
                Request.Builder().url(it).get().build()
            }
            ?.let {
                okHttpClient.newCall(it).execute().body?.string()
            }
            ?.let {
                gson.fromJson(it, method.genericReturnType)
            }


    private fun parseUrl(acc: String, pair: Pair<Array<Annotation>, Any>) =
        pair.first.filterIsInstance<Field>()
            // 取出第一个注解
            .first()
            .let { field ->
                if (acc.contains("?")) {
                    "$acc&${field.value}=${pair.second}"
                } else {
                    "$acc?${field.value}=${pair.second}"
                }
            }


}