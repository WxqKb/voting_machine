package com.example.voting_machine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import qiuxiang.android_window.WindowService

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, WindowService::class.java)
        intent.putExtra("entry", "androidWindow")
        intent.putExtra("width", 600)
        intent.putExtra("height", 800)
        intent.putExtra("x", 0)
        intent.putExtra("y", 0)
        intent.putExtra("focusable", true)
        intent.putExtra("draggable", true)
        this.startService(intent)
        finish()
    }
}