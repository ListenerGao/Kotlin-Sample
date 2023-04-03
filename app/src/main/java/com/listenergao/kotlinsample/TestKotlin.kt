package com.listenergao.kotlinsample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TestKotlin {

    // ①②③④⑤⑥⑦⑧⑨⑩

    suspend fun getToken(): String {
        withContext(Dispatchers.IO) {
            delay(2000)
        }
        return "Token"
    }
}