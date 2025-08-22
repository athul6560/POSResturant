package com.zeezaglobal.posresturant.Retrofit

import com.zeezaglobal.posresturant.Retrofit.data.SaleRequest
import com.zeezaglobal.posresturant.Retrofit.data.SaleResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("groups")
    suspend fun getGroups(): Response<List<GroupResponse>>

    @POST("sales")
    fun createSale(@Body saleRequest: SaleRequest): Call<SaleResponse>
}