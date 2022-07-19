package com.example.heartratemonitoringapp.dashboard.home

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.FragmentHomeBinding
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


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModel()

    private lateinit var lineList: ArrayList<Entry>
    private lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.localData.isNotEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                for (data in viewModel.localData) {
                    Log.d("sending", "sending")
                    viewModel.sendData(
                        viewModel.getBearer().first().toString(),
                        data.avgHeartRate ?: 0,
                        data.stepChanges ?: 0,
                        data.step ?: 0,
                        data.label.toString(),
                        data.createdAt.toString()
                    )
                    data.id?.let  { viewModel.deleteData(it) }
                }
            }
        }

        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)

        if (connectedDevices.isNotEmpty()) {
            if (BLE.bluetoothDevice == null) {
                BLE.bluetoothDevice = connectedDevices.first()
                bluetoothManager.adapter.getRemoteDevice(BLE.bluetoothDevice?.address)
                if (!BackgroundMonitoringService.isRunning) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        if (BLE.bluetoothDevice != null && viewModel.backgroundMonitoringState.first()) {
                            activity?.startService(Intent(activity, BackgroundMonitoringService::class.java))
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val bearer = viewModel.getBearer().first().toString()
            val format = DateTimeFormatter.ofPattern("yyyy-MM-d H:mm:ss")
            val startFormat = DateTimeFormatter.ofPattern("yyyy-MM-d 00:00:00")
            val start = LocalDate.now().format(startFormat)
            val end = LocalDateTime.now().format(format)
            viewModel.getAverage(bearer)
            viewModel.getDataByDate(viewModel.getBearer().first().toString(), start, end)
            getDataObserver()
            averageDataObserver()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val bearer = viewModel.getBearer().first().toString()
                val format = DateTimeFormatter.ofPattern("yyyy-MM-d H:mm:ss")
                val startFormat = DateTimeFormatter.ofPattern("yyyy-MM-d 00:00:00")
                val start = LocalDate.now().format(startFormat)
                val end = LocalDateTime.now().format(format)
                viewModel.getAverage(bearer)
                viewModel.getDataByDate(viewModel.getBearer().first().toString(), start, end)
                getDataObserver()
                averageDataObserver()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.layoutEmergencyButton.btnEmergency.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.sendNotification(viewModel.getBearer().first().toString(), 4)
                sendNotificationObserver()
            }
        }
    }

    private fun sendNotificationObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collect { res ->
                when (res) {
                    is Resource.Success -> {
                        Toast.makeText(activity, res.data.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        Toast.makeText(activity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getDataObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutAverage.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        if (!res.data.isNullOrEmpty()) {
                            binding.tvNotFound.visibility = View.GONE
                            res.data?.let { populateChart(it) }
                            val label = res.data?.first()?.label
                            if (label != null) {
                                binding.layoutAverage.tvHeartCondition.text = getString(R.string.heart_condition, label)
                            } else {
                                binding.layoutAverage.tvHeartCondition.visibility = View.INVISIBLE
                            }
                        } else {
                            binding.layoutAverage.root.visibility = View.GONE
                            binding.tvNotFound.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.layoutAverage.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(activity, res.message.toString(), Toast.LENGTH_SHORT).show()
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
            lineDataSet.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.primary_dark_color), 250)
            lineDataSet.fillColor = ContextCompat.getColor(activity!!.applicationContext, R.color.primary_dark_color)
            lineDataSet.valueTextColor = Color.BLACK
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineDataSet.setDrawFilled(true)
            lineDataSet.setDrawValues(false)
            lineDataSet.setDrawHighlightIndicators(true)
            Collections.sort(lineList, EntryXComparator())
        }
    }

    private fun averageDataObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.average.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutAverage.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        if (viewModel.backgroundMonitoringState.first()) {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutEmergencyButton.root.visibility = View.VISIBLE
                        } else {
                            binding.layoutLoading.root.visibility = View.GONE
                            binding.layoutAverage.root.visibility = View.VISIBLE
                            binding.layoutAverage.tvAvgHeartText.text = getString(R.string.today_average_heart_rate, res.data?.avgHeartRate)
                            binding.layoutAverage.tvTodayStepsValue.text = res.data?.todaySteps.toString()
                        }
                    }
                    is Resource.Error -> {
                        binding.layoutAverage.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(activity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}