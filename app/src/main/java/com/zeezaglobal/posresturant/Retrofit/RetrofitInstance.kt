package com.zeezaglobal.posresturant.Retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val client by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder().addInterceptor(logging).build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9091/api/") // For emulator (use localhost:9091 if real device with port forwarding)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}