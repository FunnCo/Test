package com.funnco.scheduler.data.retrofit.api

import com.funnco.scheduler.data.model.UserModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface SupabaseUserAPI {

    @GET("/rest/v1/profiles")
    fun getUsers(@Header("apiKey") key: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJzcW1seHh2YW1tb2Rldnl0amN3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTQ3MTkwMTIsImV4cCI6MjAxMDI5NTAxMn0.PRWufN_K8iyZdvGmbvStQolt6NP2OdSSR6MRdlyK4oo"): Call<List<UserModel>>

}