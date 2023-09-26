package com.funnco.scheduler.domain.interactor

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserDataInteractor {
    fun subscribeToUserUpdate(viewModel: ViewModel, callback: (List<UserModel>) -> Unit) {
        GlobalScope.launch {
            var previousSyncPassed = true
            while (true) {
                if (previousSyncPassed) {
                    previousSyncPassed = false
                    UserRepository.getAllUsers {
                        callback(it)
                        previousSyncPassed = true
                    }
                }
                delay(5000)
            }
        }
    }
}