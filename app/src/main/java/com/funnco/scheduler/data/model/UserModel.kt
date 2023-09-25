package com.funnco.scheduler.data.model

import com.google.gson.annotations.SerializedName


data class UserModel(
    val id: Int,
    @SerializedName("nick_name")
    val nickName: String
)
