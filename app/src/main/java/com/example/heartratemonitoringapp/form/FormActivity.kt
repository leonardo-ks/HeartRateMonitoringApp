package com.example.heartratemonitoringapp.form

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.children
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.ActivityFormBinding
import com.example.heartratemonitoringapp.databinding.ActivityMainBinding
import com.example.heartratemonitoringapp.form.adapter.FormAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.schedule

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private var mAdapter = FormAdapter()
    private val viewModel: FormViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.extras != null) {
            val avgHeart = intent.extras?.getInt("avgHeart")
            val stepChanges = intent.extras?.getInt("stepChanges")
            val step = intent.extras?.getInt("step")
            if (avgHeart != null && stepChanges != null) {
                lifecycleScope.launch {
                    viewModel.findData(viewModel.getBearer().first().toString(), avgHeart, stepChanges)
                    getLabelObserver()
                }
            }
        }

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

    private fun getLabelObserver() {
        lifecycleScope.launch {
            viewModel.labels.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutFormSelectActivity.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        binding.layoutFormSelectActivity.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        if (res.data?.isNotEmpty() == true) {
                            res.data?.let { mAdapter.setData(it) }
                            mAdapter.setCheckbox(false)
                            showRecycleView()
                        } else {
                            binding.layoutFormQuestionnaire.root.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.layoutFormSelectActivity.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@FormActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showRecycleView() {
        binding.layoutFormSelectActivity.rvLabels.apply {
            layoutManager = LinearLayoutManager(this@FormActivity)
            !hasFixedSize()
            adapter = mAdapter
        }
    }
}