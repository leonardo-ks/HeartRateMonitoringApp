package com.example.heartratemonitoringapp.dashboard.profile.contact.showcontactdata

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.dashboard.profile.contact.add.AddContactViewModel
import com.example.heartratemonitoringapp.databinding.ActivityAddContactBinding
import com.example.heartratemonitoringapp.databinding.ActivityContactBinding
import com.example.heartratemonitoringapp.databinding.ActivityShowContactDataBinding
import com.example.heartratemonitoringapp.monitoring.background.BackgroundMonitoringService
import com.example.heartratemonitoringapp.monitoring.ble.BLE
import com.example.heartratemonitoringapp.util.HourValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ShowContactDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowContactDataBinding
    private val viewModel: ShowContactViewModel by viewModel()

    private lateinit var lineList: ArrayList<Entry>
    private lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData

    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowContactDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        if (intent.extras != null) {
            val name = intent.extras?.getString("name") ?: ""
            id = intent.extras?.getInt("id")!!
            binding.toolbar.title = getString(R.string.show_contact_data_title, name)
        }

        lifecycleScope.launch {
            val bearer = viewModel.getBearer().first()
            viewModel.getAverageById(bearer.toString(), id)
            val format = DateTimeFormatter.ofPattern("yyyy-MM-d H:mm:ss")
            val startFormat = DateTimeFormatter.ofPattern("yyyy-MM-d 00:00:00")
            val start = LocalDate.now().format(startFormat)
            val end = LocalDateTime.now().format(format)
            viewModel.getDataByDateById(bearer.toString(), id, start, end)
            getDataObserver()
            averageDataObserver()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                val bearer = viewModel.getBearer().first()
                viewModel.getAverageById(bearer.toString(), id)
                val format = DateTimeFormatter.ofPattern("yyyy-MM-d H:mm:ss")
                val startFormat = DateTimeFormatter.ofPattern("yyyy-MM-d 00:00:00")
                val start = LocalDate.now().format(startFormat)
                val end = LocalDateTime.now().format(format)
                viewModel.getDataByDateById(bearer.toString(), id, start, end)
                getDataObserver()
                averageDataObserver()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun averageDataObserver() {
        lifecycleScope.launch {
            viewModel.average.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutAverage.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        if (res.data != null) {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutAverage.root.visibility = View.VISIBLE
                            binding.layoutAverage.tvAvgHeartText.text = getString(R.string.today_average_heart_rate, res.data?.avgHeartRate)
                            binding.layoutAverage.tvTodayStepsValue.text = res.data?.todaySteps.toString()
                        } else {
                            binding.tvDataNotFound.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.layoutAverage.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@ShowContactDataActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getDataObserver() {
        lifecycleScope.launch {
            viewModel.data.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutAverage.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        res.data?.let { populateChart(it) }
                        binding.layoutAverage.tvLastCondition.text = getString(R.string.last_condition, res.data?.first()?.label)
                    }
                    is Resource.Error -> {
                        binding.layoutAverage.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@ShowContactDataActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun populateChart(list: List<MonitoringDataDomain>) {
        val lineChart = binding.layoutAverage.lineChartHeartRate
        lineList = ArrayList()
        if (list.isNotEmpty()) {
            for (data in list) {
                val format = DateTimeFormatter.ofPattern("d-MM-yyyy H:mm:ss")
                val time = LocalDateTime.parse(data.createdAt.toString(), format)
                val x = "${time.hour}.${time.minute}".toFloat()
                val y = data.avgHeartRate!!.toFloat()
                lineList.add(Entry(x, y))
            }
            lineDataSet = LineDataSet(lineList, "test")
            lineData = LineData(lineDataSet)
            lineChart.data = lineData
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.xAxis.valueFormatter = HourValueFormatter()
            lineChart.xAxis.setLabelCount(4, true)
            lineChart.xAxis.axisMinimum = 0F
            lineChart.xAxis.axisMaximum = 23.59F
            lineChart.legend.isEnabled = false
            lineChart.description.isEnabled = false
            lineChart.axisRight.isEnabled = false
            lineChart.isDoubleTapToZoomEnabled = false
            lineDataSet.setColor(ContextCompat.getColor(applicationContext, R.color.primary_dark_color), 250)
            lineDataSet.fillColor = ContextCompat.getColor(applicationContext, R.color.primary_dark_color)
            lineDataSet.valueTextColor = Color.BLACK
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineDataSet.setDrawFilled(true)
            lineDataSet.setDrawValues(false)
            lineDataSet.setDrawHighlightIndicators(true)
            Collections.sort(lineList, EntryXComparator())
        }
    }
}