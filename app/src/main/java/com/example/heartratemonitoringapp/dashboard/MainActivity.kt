package com.example.heartratemonitoringapp.dashboard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.ActivityMainBinding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch{
            viewModel.getProfile(viewModel.getBearer().first().toString())
            userProfileObserver()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        binding.navView.apply {
            itemIconTintList = null
            setupWithNavController(navHostFragment.navController)
        }
    }

    private fun userProfileObserver() {
        lifecycleScope.launch {
            viewModel.profile.collect {res ->
                when (res) {
                    is Resource.Success -> {
                        val topic = "hrm${res.data?.name?.lowercase(Locale.getDefault())}"
                        Log.d("topic", topic)
                        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    }
                    else -> {}
                }
            }
        }
    }
}