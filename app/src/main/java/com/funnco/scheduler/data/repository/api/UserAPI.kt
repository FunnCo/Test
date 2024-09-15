package com.funnco.scheduler.data.repository.api

import com.funnco.scheduler.data.repository.model.UserModel
import org.example.common.restutils.GET
import org.example.common.restutils.RestClient

@RestClient(baseUrl = "http://188.120.240.201:8080/api")
interface UserAPI {

    @GET("users")
    suspend fun getAllUsers(): List<UserModel>
}