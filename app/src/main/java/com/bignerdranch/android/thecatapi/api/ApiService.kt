package com.bignerdranch.android.thecatapi.api

import com.bignerdranch.android.thecatapi.models.Cat
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @Headers("x-api-key: $API_KEY")
    @GET("v1/images/search")
    suspend fun getPhotos(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
    ): Response<List<Cat>>

    companion object {
        const val BASE_URL = "https://api.thecatapi.com/"
        const val API_KEY = "b5c1f8a8-c6f7-4372-b148-80bc3d124043"
    }
}