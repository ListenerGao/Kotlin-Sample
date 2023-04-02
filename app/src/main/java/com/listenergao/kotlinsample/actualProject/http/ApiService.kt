package com.listenergao.kotlinsample.actualProject.http

import com.listenergao.kotlinsample.actualProject.http.annoatation.Field
import com.listenergao.kotlinsample.actualProject.http.annoatation.GET
import com.listenergao.kotlinsample.actualProject.http.model.KnowledgeHierarchyResp

interface ApiService {

    // https://www.wanandroid.com/article/list/0/json?cid=60
    @GET("/article/list/0/json")
    fun getArticle(@Field("cid") cid: Int): KnowledgeHierarchyResp
}