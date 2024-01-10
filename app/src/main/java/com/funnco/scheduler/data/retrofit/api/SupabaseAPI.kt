package com.funnco.scheduler.data.retrofit.api


import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.data.retrofit.RetrofitInstance
import com.funnco.scheduler.data.retrofit.dto.ScheduleDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SupabaseAPI {

    @GET("/rest/v1/profiles")
    fun getUsers(@Header("apiKey") key: String = RetrofitInstance.API_KEY): Call<List<UserModel>>

    @GET("/rest/v1/schedule")
    fun getSchedules(@Header("apiKey") key: String = RetrofitInstance.API_KEY): Call<List<ScheduleDTO>>

    @POST("/rest/v1/schedule")
    fun postSchedules(@Body schedules: List<ScheduleDTO>, @Header("Prefer") preference: String = "resolution=merge-duplicates, return=representation",@Header("apiKey") key: String = RetrofitInstance.API_KEY): Call<List<ScheduleDTO>>
}