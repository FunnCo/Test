package com.funnco.scheduler.data.retrofit

import com.funnco.scheduler.data.retrofit.api.SupabaseUserAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val baseUrl = "https://rsqmlxxvammodevytjcw.supabase.co/"

    private val retrofit by lazy{
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    }

    val userAPI by lazy {
        retrofit.create(SupabaseUserAPI::class.java)
    }
}