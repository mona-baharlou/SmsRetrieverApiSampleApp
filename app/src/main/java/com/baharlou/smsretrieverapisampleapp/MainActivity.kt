package com.baharlou.smsretrieverapisampleapp

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import timber.log.Timber

class MainActivity : AppCompatActivity(), SmsReceiver.Companion.OTPReceiveListener {

    private var smsReceiver: SmsReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //delete this class after generating hash code
        val appSignatureHashHelper = AppSignatureHashHelper(this)
        // This code requires one time to get Hash keys do comment and share key
        Timber.i(
            "HashKey: ${appSignatureHashHelper.appSignatures[0]}"
        )

        startSMSListener()
    }


    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    private fun startSMSListener() {
        try {
            smsReceiver = SmsReceiver()
            smsReceiver!!.setOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(this)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }
            task.addOnFailureListener {
                // Fail to start API
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOTPReceived(otp: String?) {
        showToast("OTP Received: $otp")

        if (smsReceiver != null) {
           unregisterReceiver(smsReceiver)
            smsReceiver = null
        }
    }

    override fun onOTPTimeOut() {
        showToast("OTP Timed out")

    }

    override fun onOTPReceivedError(error: String?) {
        showToast("OTP error occured  $error")

    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(smsReceiver != null)
            unregisterReceiver(smsReceiver)
    }

}
