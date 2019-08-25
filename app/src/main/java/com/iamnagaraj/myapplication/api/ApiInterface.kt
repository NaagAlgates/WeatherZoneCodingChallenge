package com.iamnagaraj.myapplication.api

import com.iamnagaraj.myapplication.BuildConfig
import com.iamnagaraj.myapplication.model.PexelDataModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiInterface{
    @GET(BuildConfig.SEARCH_PATH)
    fun fetchApiData(
        @Header("Authorization") authorization:String = BuildConfig.API_KEY,
        @Query(BuildConfig.PER_PAGE,encoded = true) perPage: Int,
        @Query(BuildConfig.PAGE_COUNT,encoded = true) pageCount: Int,
        @Query(BuildConfig.SEARCH_QUERY,encoded = true) searchQuery: String
    ): Observable<PexelDataModel>
}