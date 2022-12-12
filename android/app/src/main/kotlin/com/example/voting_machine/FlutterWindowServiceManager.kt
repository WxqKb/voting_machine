package com.example.voting_machine

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.cvte.maxhub.log.NLog
import com.maxhub.os.flutter_multi_windows.FlutterWTListener
import com.maxhub.os.flutter_multi_windows.FlutterWindowService
import com.maxhub.os.flutter_multi_windows.WindowService
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object FlutterWindowServiceManager {

    private lateinit var context: Context
    private var countDownLatch: CountDownLatch? = null

    private var windowServiceImpl: FlutterWindowService? = null

    // 任务和id如何设置精准
    private var taskIsRunning: Boolean = false
    private var currTaskId: String? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            NLog.i(TAG, "onServiceConnected")
            try {
                windowServiceImpl = service as? FlutterWindowService
                countDownLatch?.countDown()
            } catch (e: Exception) {
                Log.e(TAG, "onServiceConnected: $e")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            NLog.i(TAG, "onServiceDisconnected")
            windowServiceImpl = null
            countDownLatch = CountDownLatch(1)
        }
    }

    fun init(context: Context) {
        NLog.i(TAG, "init")
        countDownLatch = CountDownLatch(1)
        val intent = Intent(context, WindowService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun release() {
        NLog.i(TAG, "release")
        context.unbindService(serviceConnection)
    }

    @Synchronized
    fun executeTask(intent: Intent) {
        if (taskIsRunning) {
            NLog.i(TAG, "Task is running")
            return
        }
        NLog.i(TAG, "executeTask: intent=$intent, extras=${intent.extras}")
        try {
            if (windowServiceImpl == null) {
                NLog.e(TAG, "executeTask: windowServiceImpl is null, await!")
                countDownLatch?.await(10, TimeUnit.SECONDS)
            }
            taskIsRunning = true
            Handler(Looper.getMainLooper()).post {
                // 该方法必须在主线程执行
                currTaskId =
                    windowServiceImpl?.showWindow(intent,

                        object : FlutterWTListener.Stub() {
                            override fun onStart() {
                            }

                            override fun onStop() {
                                NLog.i(TAG, "Task is stop")
                                taskIsRunning = false
                                currTaskId = null
                            }
                        })
            }
        } catch (e: Exception) {
            Log.e(TAG, "executeTask: $e")
        }
    }

    fun stopCurrTask() {
        if (taskIsRunning && currTaskId != null) {
            Handler(Looper.getMainLooper()).post {
                taskIsRunning = false
                windowServiceImpl?.dissmissWindow(currTaskId)
                currTaskId = null
            }
        }
    }

    private const val TAG = "FlutterWindowServiceManager"
}