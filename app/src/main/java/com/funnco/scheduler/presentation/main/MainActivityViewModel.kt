package com.funnco.scheduler.presentation.main

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.data.retrofit.RetrofitInstance
import com.funnco.scheduler.domain.interactor.UserDataInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel: ViewModel() {

    val userDataInteractor = UserDataInteractor()
    val listOfUsers = MutableStateFlow(emptyList<UserModel>())

    init {
        userDataInteractor.subscribeToUserUpdate(this) {
            listOfUsers.value = it
        }
    }

}