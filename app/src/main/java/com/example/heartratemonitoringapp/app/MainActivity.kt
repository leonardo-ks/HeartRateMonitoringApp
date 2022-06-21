package com.example.heartratemonitoringapp.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.data.source.remote.network.FirebaseService
import com.example.heartratemonitoringapp.databinding.ActivityMainBinding
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().subscribeToTopic("topic")
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("token", it.toString())
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        binding.navView.apply {
            itemIconTintList = null
            setupWithNavController(navHostFragment.navController)
        }
    }
}