package com.listenergao.kotlinsample.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.listenergao.kotlinsample.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            log("IO scope...")
        }

        val mainScope = CoroutineScope(Dispatchers.Main)
        mainScope.launch {
            log("Main scope...")
        }
        val mainScope1 = MainScope()
        mainScope1.launch {
            log("Main scope1...")
        }
    }

    private fun log(msg: String) {
        Log.d("gys", "currentThread:${Thread.currentThread().name}, $msg")
    }
}