package com.zeezaglobal.posresturant.Retrofit

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("api/groups")
    suspend fun getGroups(): Response<List<GroupResponse>>
}