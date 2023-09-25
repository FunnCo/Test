package com.funnco.scheduler.presentation.main

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.data.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel: ViewModel() {

    val listOfUsers = MutableStateFlow(emptyList<UserModel>())

    init {
        RetrofitInstance.userAPI.getUsers().enqueue(object: Callback<List<UserModel>> {
            override fun onResponse(
                call: Call<List<UserModel>>,
                response: Response<List<UserModel>>
            ) {
                Log.i("TAG", "Success ${response.raw()}")
                listOfUsers.value = response.body()!!
            }

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                Log.e("TAG", "Fail ${t.message}")
            }

        })
    }

}