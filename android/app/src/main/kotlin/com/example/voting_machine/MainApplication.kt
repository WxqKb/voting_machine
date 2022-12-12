package com.example.voting_machine

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.cvte.maxhub.log.NLog
import io.flutter.FlutterInjector

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FlutterInjector.instance().flutterLoader().startInitialization(this)
        instance = this
        context = applicationContext
        NLog.init(NLog.MODE.SIMPLE)
        NLog.i(TAG, "onCreate")

        FlutterWindowServiceManager.init(context)
    }

    companion object {
        private const val TAG = "MainApplication"

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MainApplication

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}