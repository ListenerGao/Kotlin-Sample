package com.listenergao.kotlinsample.actualProject.http.annoatation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Field(val value: String)
