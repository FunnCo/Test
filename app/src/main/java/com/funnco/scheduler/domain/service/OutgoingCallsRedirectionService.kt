package com.funnco.scheduler.domain.service

import android.app.AlertDialog
import android.net.Uri
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import android.util.Log
import android.view.WindowManager

class OutgoingCallsRedirectionService : CallRedirectionService() {

    val testNumber = "79052234755"
    override fun onPlaceCall(
        uri: Uri,
        phoneAccountHandle: PhoneAccountHandle,
        allowInteractiveResponse: Boolean
    ) {

        if (uri.toString().removeRange(0, 7) == testNumber) {
            cancelCall()
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Абонент занят")
            alertDialogBuilder.setMessage("К сожлению, по расписанию из приложения Scheduler, вызываемый абонент на данный момент занят, и не может ответить вам. Попробуйте перезвонить позднее, или свяжитесь с ним другим способом.")
            alertDialogBuilder.setNeutralButton("Понятно") { dialog, _ ->
                dialog.dismiss()
            }
            val alert: AlertDialog = alertDialogBuilder.create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.show()
        }
    }
}