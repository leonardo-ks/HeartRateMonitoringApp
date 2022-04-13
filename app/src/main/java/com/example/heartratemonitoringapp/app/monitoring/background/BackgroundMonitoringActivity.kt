package com.example.heartratemonitoringapp.app.monitoring.background

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.heartratemonitoringapp.databinding.ActivityBackgroundMonitoringBinding

class BackgroundMonitoringActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackgroundMonitoringBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackgroundMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnStartService.setOnClickListener {
            startService(Intent(this, BackgroundMonitoringService::class.java))
        }

        binding.btnStopService.setOnClickListener {
            stopService(Intent(this, BackgroundMonitoringService::class.java))
        }
    }
}