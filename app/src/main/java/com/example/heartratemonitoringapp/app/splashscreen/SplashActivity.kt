package com.example.heartratemonitoringapp.app.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.app.MainActivity
import com.example.heartratemonitoringapp.app.auth.login.LoginActivity
import com.example.heartratemonitoringapp.databinding.ActivitySplashBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModel()
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.splashIcon.apply {
            alpha = 0f
            animate().alpha(1f).setDuration(1300).start()
        }

        binding.splashText.apply {
            alpha = 0f
            animate().alpha(1f).setDuration(1300).start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.isLogin.collect {
                    if (it) {
                        val savedLoginDate = viewModel.latestLoginDate.first()
                        if (!savedLoginDate.isNullOrBlank()) {
                            val latestLoginDate = LocalDateTime.parse(savedLoginDate)
                            val now = LocalDateTime.now()
                            if (now.minusDays(7).isAfter(latestLoginDate)) {
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }, 1500)

    }
}