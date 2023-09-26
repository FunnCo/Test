package com.funnco.scheduler.data.repository

import android.util.Log
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import com.funnco.scheduler.data.retrofit.RetrofitInstance
import com.funnco.scheduler.data.retrofit.dto.ScheduleDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.util.stream.Collectors

object UserRepository {

        fun getAllSchedules(callback: (List<ScheduleModel>) -> Unit) {
            RetrofitInstance.supabaseAPI.getSchedules()
                .enqueue(object : Callback<List<ScheduleDTO>> {
                    override fun onResponse(
                        call: Call<List<ScheduleDTO>>,
                        response: Response<List<ScheduleDTO>>
                    ) {
                        Log.i("TAG1", "Success ${response.raw()}")
                        callback(response.body()!!.stream().map { dto ->
                            ScheduleModel(
                                userId = dto.userId,
                                dayNumber = dto.dayNumber,
                                startTime = LocalTime.parse(dto.startTime),
                                endTime = LocalTime.parse(dto.endTime),
                            )
                        }.collect(Collectors.toList()))
                    }

                    override fun onFailure(call: Call<List<ScheduleDTO>>, t: Throwable) {
                        Log.e("TAG1", "Fail ${t.message}")
                    }

                })
        }

        fun getAllUsers(callback: (List<UserModel>) -> Unit) {
            RetrofitInstance.supabaseAPI.getUsers().enqueue(object : Callback<List<UserModel>> {
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