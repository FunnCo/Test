package com.funnco.scheduler.data.retrofit

import com.funnco.scheduler.data.retrofit.api.SupabaseAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val BASE_URL = "https://rsqmlxxvammodevytjcw.supabase.co/"
    val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJzcW1seHh2YW1tb2Rldnl0amN3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTQ3MTkwMTIsImV4cCI6MjAxMDI5NTAxMn0.PRWufN_K8iyZdvGmbvStQolt6NP2OdSSR6MRdlyK4oo"

    private val retrofit by lazy{
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }

    val supabaseAPI by lazy {
        retrofit.create(SupabaseAPI::class.java)
    }
}