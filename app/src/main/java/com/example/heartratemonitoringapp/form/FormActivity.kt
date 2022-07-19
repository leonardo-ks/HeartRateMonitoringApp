package com.example.heartratemonitoringapp.form

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.ActivityFormBinding
import com.example.heartratemonitoringapp.dashboard.profile.contact.adapter.ContactAdapter
import com.example.heartratemonitoringapp.form.adapter.FormAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private var mAdapter = FormAdapter()
    private val viewModel: FormViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutFormSelectActivity.btnPositive.setOnClickListener {
            mAdapter.setCheckbox(true)
            binding.layoutFormSelectActivity.btnPositive.visibility = View.GONE
            binding.layoutFormSelectActivity.btnNegative.visibility = View.GONE
            binding.layoutFormSelectActivity.rvLabels.visibility = View.GONE
            binding.layoutFormSelectActivity.tvTitle.visibility = View.GONE
            binding.layoutLoading.root.visibility = View.VISIBLE
            lifecycleScope.launch {
                delay(500)
                binding.layoutFormSelectActivity.btnSend.visibility = View.VISIBLE
                binding.layoutFormSelectActivity.tvTitle.visibility = View.VISIBLE
                binding.layoutFormSelectActivity.rvLabels.visibility = View.VISIBLE
                binding.layoutLoading.root.visibility = View.GONE
                binding.layoutFormSelectActivity.tvTitle.text = getString(R.string.select_activities)
            }
        }

        binding.layoutFormQuestionnaire.rgMovement.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_movement_yes -> binding.layoutFormQuestionnaire.btnSend.isEnabled = true
                R.id.rb_movement_no -> binding.layoutFormQuestionnaire.btnSend.isEnabled = true
            }
        }

        binding.layoutFormSelectActivity.btnNegative.setOnClickListener {
            binding.layoutLoading.root.visibility = View.VISIBLE
            binding.layoutFormSelectActivity.root.visibility = View.GONE
            lifecycleScope.launch {
                delay(500)
                binding.layoutLoading.root.visibility = View.GONE
                binding.layoutFormQuestionnaire.root.visibility = View.VISIBLE
            }
        }
    }
}