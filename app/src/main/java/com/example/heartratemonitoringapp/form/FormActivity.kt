package com.example.heartratemonitoringapp.form

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.ActivityFormBinding
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
    private val labelList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var avgHeartRate: Int? = null
        var stepChanges: Int? = null
        var step: Int? = null

        showRecycleView()

        if (intent.extras != null) {
            avgHeartRate = intent.extras?.getInt("avgHeart")
            stepChanges = intent.extras?.getInt("stepChanges")
            step = intent.extras?.getInt("step")
            if (avgHeartRate != null && stepChanges != null) {
                lifecycleScope.launch {
                    viewModel.findData(viewModel.getBearer().first().toString(), avgHeartRate, stepChanges)
                    getLabelObserver()
                }
            }
        } else {
            binding.layoutFormQuestionnaire.root.visibility = View.VISIBLE
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

        binding.layoutFormSelectActivity.btnSend.setOnClickListener {
            if (labelList.isNotEmpty()) {
                for (label in labelList) {
                    if (avgHeartRate != null && stepChanges != null && step != null) {
                        sendData(avgHeartRate, stepChanges, step, label)
                    }
                }
            } else {
                if (avgHeartRate != null && stepChanges != null && step != null) {
                    sendData(avgHeartRate, stepChanges, step, "")
                }
            }
        }

        binding.layoutFormQuestionnaire.rgAddLabel.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_add_label_yes) {
                binding.layoutFormQuestionnaire.tilLabel.visibility = View.VISIBLE
            } else {
                binding.layoutFormQuestionnaire.tilLabel.visibility = View.GONE
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

        binding.layoutFormQuestionnaire.btnSend.setOnClickListener {
            if (avgHeartRate != null && stepChanges != null && step != null) {
                sendData(avgHeartRate, stepChanges, step, binding.layoutFormQuestionnaire.tidtLabel.text.toString())
            }
        }

        mAdapter.onItemClick = {label, state ->
            if (state) {
                labelList.add(label)
            } else {
                labelList.remove(label)
            }
        }
    }

    private fun sendData(avgHeartRate: Int, stepChanges: Int, step: Int, label: String) {
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.now().format(format)
        lifecycleScope.launch {
            viewModel.sendData(
                viewModel.getBearer().first().toString(),
                avgHeartRate,
                stepChanges,
                step,
                label,
                date
            )
        }
        sendDataObserver()
    }

    private fun sendDataObserver() {
        lifecycleScope.launch {
            viewModel.sendData.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutFormQuestionnaire.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        binding.layoutLoading.root.visibility = View.GONE
                        binding.layoutFormQuestionnaire.root.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        binding.layoutFormQuestionnaire.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@FormActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
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