package com.funnco.scheduler.data.repository

import android.util.Log
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.data.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserRepository {

    fun getAllUsers(callback: (List<UserModel>) -> Unit) {
        RetrofitInstance.userAPI.getUsers().enqueue(object : Callback<List<UserModel>> {
            override fun onResponse(
                call: Call<List<UserModel>>,
                response: Response<List<UserModel>>
            ) {
                Log.i("TAG", "Success ${response.raw()}")
                callback(response.body()!!)
            }

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                Log.e("TAG", "Fail ${t.message}")
            }

        })
    }
}