package com.example.heartratemonitoringapp.form

import android.os.Bundle
import android.text.TextUtils.split
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.core.data.Resource
import com.example.core.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.ActivityFormBinding
import com.example.heartratemonitoringapp.dashboard.profile.contact.adapter.ContactAdapter
import com.example.heartratemonitoringapp.form.adapter.FormAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.scope.scope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private var mAdapter = FormAdapter()
    private val viewModel: FormViewModel by viewModel()

    private var avgHeartRate = -1
    private var stepChanges = -1
    private var step = -1
    private var anomalyDetectedTimes = 0
    private var latestDate = ""
    private var minLimit = 0
    private var maxStillLimit = 0
    private var maxWalkLimit = 0
    private var maxLimitByAge = 0
    private var status = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            anomalyDetectedTimes = viewModel.anomalyDetectedTimes.first()
            latestDate = viewModel.latestAnomalyDate.first().toString()
            val date = LocalDate.parse(latestDate)
            if (date.isBefore(LocalDate.now())) {
                viewModel.setAnomalyDetectedTimes(0)
            }
            viewModel.getLimit(viewModel.getBearer().first().toString())
            viewModel.getProfile(viewModel.getBearer().first().toString())
            getLimitObserver()
            getProfileObserver()
        }

        if (intent.extras != null) {
            avgHeartRate = intent.extras?.getInt("avgHeartRate") ?: -1
            stepChanges = intent.extras?.getInt("stepChanges") ?: -1
            step = intent.extras?.getInt("step") ?: -1
            status = intent.extras?.getInt("status") ?: 0
        }

//        binding.layoutFormSelectActivity.btnPositive.setOnClickListener {
//            mAdapter.setCheckbox(true)
//            binding.layoutFormSelectActivity.btnPositive.visibility = View.GONE
//            binding.layoutFormSelectActivity.btnNegative.visibility = View.GONE
//            binding.layoutFormSelectActivity.rvLabels.visibility = View.GONE
//            binding.layoutFormSelectActivity.tvTitle.visibility = View.GONE
//            binding.layoutLoading.root.visibility = View.VISIBLE
//            lifecycleScope.launch {
//                delay(500)
//                binding.layoutFormSelectActivity.btnSend.visibility = View.VISIBLE
//                binding.layoutFormSelectActivity.tvTitle.visibility = View.VISIBLE
//                binding.layoutFormSelectActivity.rvLabels.visibility = View.VISIBLE
//                binding.layoutLoading.root.visibility = View.GONE
//                binding.layoutFormSelectActivity.tvTitle.text = getString(R.string.select_activities)
//            }
//        }

        binding.layoutFormQuestionnaire.rgMovement.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_movement_yes -> binding.layoutFormQuestionnaire.btnSend.isEnabled = true
                R.id.rb_movement_no -> binding.layoutFormQuestionnaire.btnSend.isEnabled = true
            }
        }

//        binding.layoutFormSelectActivity.btnNegative.setOnClickListener {
//            binding.layoutLoading.root.visibility = View.VISIBLE
//            binding.layoutFormSelectActivity.root.visibility = View.GONE
//            lifecycleScope.launch {
//                delay(500)
//                binding.layoutLoading.root.visibility = View.GONE
//                binding.layoutFormQuestionnaire.root.visibility = View.VISIBLE
//            }
//        }

        binding.layoutFormQuestionnaire.btnSend.setOnClickListener {
            lifecycleScope.launch {
                viewModel.setAnomalyDetectedTimes(viewModel.anomalyDetectedTimes.first() + 1)
                viewModel.setLatestAnomalyDate(LocalDate.now().toString())
            }
            when (status) {
                1 -> {
                    if (binding.layoutFormQuestionnaire.rbMovementYes.isChecked) {
                        sendData(avgHeartRate, stepChanges, step, "Normal")
                    } else {
                        sendData(avgHeartRate, stepChanges, step, "Tidak normal 1")
                    }
                }
                2 -> sendData(avgHeartRate, stepChanges, step, "Tidak normal 2")

                3 -> {
                    if (binding.layoutFormQuestionnaire.rbMovementYes.isChecked) {
                        sendData(avgHeartRate, stepChanges, step, "Tidak normal 3")
                    } else {
                        sendData(avgHeartRate, stepChanges, step, "Normal")
                    }
                }
                4 -> sendData(avgHeartRate, stepChanges, step, "Tidak normal 4")
            }
        }
    }

    private fun sendData(avgHeart: Int, stepChanges: Int, step: Int, label: String) {
        lifecycleScope.launch {
            val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val date = LocalDateTime.now().format(format)
            viewModel.insertMonitoringData(
                MonitoringDataDomain(
                    avgHeartRate = avgHeart,
                    stepChanges = stepChanges,
                    createdAt = date.toString(),
                    label = label,
                    userId = viewModel.getUserId().first(),
                    step = step
                )
            )
            viewModel.sendData(viewModel.getBearer().first().toString(), avgHeart, stepChanges, step, label, date)
            viewModel.sendData.collect {
                when (it) {
                    is Resource.Success -> {
                        viewModel.deleteMonitoringDataByDate(date)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getProfileObserver() {
        lifecycleScope.launch {
            viewModel.profile.collect { res ->
                when (res) {
                    is Resource.Success -> {
                        val dob = res.data?.dob.toString()
                        val format = DateTimeFormatter.ofPattern("d-MM-yyyy")
                        val formatted = LocalDate.parse(dob).format(format)
                        val now = LocalDate.now()
                        maxLimitByAge = ((220 - Period.between(LocalDate.parse(formatted, format), now).years) * 0.85).toInt()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getLimitObserver() {
        lifecycleScope.launch {
            viewModel.limit.collect() {
                when (it) {
                    is Resource.Success -> {
                        minLimit = it.data?.lower!!
                        maxWalkLimit = it.data?.upperWalk!!
                        maxStillLimit = it.data?.upperStill!!
                    }
                    else -> {}
                }
            }
        }
    }
}