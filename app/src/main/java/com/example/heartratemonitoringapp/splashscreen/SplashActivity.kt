package com.example.heartratemonitoringapp.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.dashboard.MainActivity
import com.example.heartratemonitoringapp.auth.login.LoginActivity
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
                                logout(viewModel.getBearer().first().toString())
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

    private fun logout(bearer: String) {
        lifecycleScope.launch {
            viewModel.logout(bearer).collect {
                when (it) {
                    is Resource.Success -> startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    else -> Toast.makeText(this@SplashActivity, getString(R.string.logout_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}