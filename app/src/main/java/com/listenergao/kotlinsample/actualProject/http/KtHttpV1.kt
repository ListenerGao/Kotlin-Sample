package com.listenergao.kotlinsample.actualProject.http

import com.google.gson.Gson
import com.listenergao.kotlinsample.actualProject.http.annoatation.Field
import com.listenergao.kotlinsample.actualProject.http.annoatation.GET
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Method
import java.lang.reflect.Proxy

fun main() {
    KtHttpV1.baseUrl = "https://www.wanandroid.com"

    val apiService = KtHttpV1.create(ApiService::class.java)
    val articleInfo = apiService.getArticle(cid = 60)
    println(articleInfo)
}

object KtHttpV1 {
    private val okHttpClient = OkHttpClient()
    private val gson = Gson()

    var baseUrl = ""


    fun <T> create(service: Class<T>): T {

        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service)
        ) { proxy, method, args ->

            if (baseUrl.isEmpty()) {
                throw NullPointerException("Host not set...")
            }

            val annotations = method.annotations
            for (annotation in annotations) {
                if (annotation is GET) {
                    val url = baseUrl + annotation.value
                    return@newProxyInstance invoke(url, method, args)
                }
            }

            return@newProxyInstance null

        } as T
    }

    private fun invoke(url: String, method: Method, args: Array<Any>): Any? {
        if (method.parameterAnnotations.size != args.size) {
            return null
        }

        var fullUrl = url

        val parameterAnnotations = method.parameterAnnotations
        for (i in parameterAnnotations.indices) {
            for (parameterAnnotation in parameterAnnotations[i]) {
                if (parameterAnnotation is Field) {
                    val key = parameterAnnotation.value
                    val value = args[i].toString()

                    fullUrl += if (url.contains("?")) {
                        "&$key=$value"
                    } else {
                        "?$key=$value"
                    }
                }
            }
        }

        val request = Request.Builder()
            .url(fullUrl)
            .build()
        val response = okHttpClient.newCall(request).execute()

        val result = gson.fromJson<Any?>(response.body?.string(), method.genericReturnType)
        return result

    }


}