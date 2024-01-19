package com.funnco.scheduler.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName


data class UserModel(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("nickName")
    val nickName: String
)
