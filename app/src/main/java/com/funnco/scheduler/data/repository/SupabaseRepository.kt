package com.funnco.scheduler.data.repository

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JSR310Module
import com.funnco.scheduler.data.model.ScheduleModel
import com.funnco.scheduler.data.model.UserModel
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.serializer.JacksonSerializer
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat

object SupabaseRepository {

    val supabase = createSupabaseClient(
        supabaseUrl = "https://rsqmlxxvammodevytjcw.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJzcW1seHh2YW1tb2Rldnl0amN3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTQ3MTkwMTIsImV4cCI6MjAxMDI5NTAxMn0.PRWufN_K8iyZdvGmbvStQolt6NP2OdSSR6MRdlyK4oo"
    ) {
        this.install(Postgrest)
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JSR310Module())
        objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        defaultSerializer = JacksonSerializer(objectMapper)
    }

    suspend fun deleteSchedule (modelsToDelete: List<ScheduleModel> ){
        supabase.from("schedule").delete {
            filter {
                ScheduleModel::id isIn modelsToDelete.map { it.id }
            }
        }
    }

    suspend fun upsertSchedules(newSchedule: List<ScheduleModel>, callback: (Boolean) -> Unit) {
        try {
            supabase.from("schedule").upsert(newSchedule) {
                delay(15)
                callback(true)
            }
        } catch (exception: Exception){
            Log.e("TAGGG", "Error occurred: ${exception}")
            callback(false)
        }
    }

    suspend fun getAllSchedules(callback: (List<ScheduleModel>) -> Unit) {
        val schedules = supabase.from("schedule").select().decodeList<ScheduleModel>()
        callback(schedules)
    }

    suspend fun getAllUsers(callback: (List<UserModel>) -> Unit) {
        val users = supabase.from("profiles").select().decodeList<UserModel>()
        callback(users)
    }
}