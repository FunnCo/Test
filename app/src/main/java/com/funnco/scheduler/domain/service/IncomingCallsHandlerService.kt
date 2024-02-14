package com.funnco.scheduler.domain.service

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log

class IncomingCallsHandlerService : InCallService() {
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        call?.disconnect()
        Log.d("IncomingCallsHandlerService", "Call received and dismissed")
    }
}