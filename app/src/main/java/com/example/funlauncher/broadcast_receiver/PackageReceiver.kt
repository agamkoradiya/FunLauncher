package com.example.funlauncher.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

const val TAG = "PackageReceiver"

class PackageReceiver : BroadcastReceiver() {

    companion object {
        private val _isRefreshNeeded: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
        val isRefreshNeeded: LiveData<Boolean> = _isRefreshNeeded
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: called")

        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                Log.d(TAG, "onReceive: ACTION_PACKAGE_ADDED")
                if (_isRefreshNeeded.value == true) {
                    _isRefreshNeeded.value = false
                }
                _isRefreshNeeded.value = true
            }
            Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                Log.d(TAG, "onReceive: ACTION_PACKAGE_REMOVED")
                if (_isRefreshNeeded.value == true) {
                    _isRefreshNeeded.value = false
                }
                _isRefreshNeeded.value = true
            }
        }
    }
}