package com.listenergao.kotlinsample.actualProject.http.annoatation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GET(val value: String)
