package org.example.common.restutils

import kotlinx.coroutines.*

class RepetitiveRequestManager {
    private val jobs = mutableMapOf<String, Job>()

    fun executeRepetitive(
        methodName: String,
        method: suspend () -> Unit,
        repeatInterval: Long
    ) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                method()
                delay(repeatInterval)
            }
        }
        jobs[methodName] = job
    }

    fun stopRepetitive(methodName: String) {
        if(jobs.containsKey(methodName)){
            jobs[methodName]?.cancel()
            jobs.remove(methodName)
        }
    }
}