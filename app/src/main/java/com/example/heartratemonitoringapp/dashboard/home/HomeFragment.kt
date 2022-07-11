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
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.FragmentHomeBinding
import com.example.heartratemonitoringapp.monitoring.background.BackgroundMonitoringService
import com.example.heartratemonitoringapp.monitoring.ble.BLE
import com.example.heartratemonitoringapp.scanner.ScannerActivity
import com.example.heartratemonitoringapp.util.HourValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random


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
                        data.createdAt.toString()
                    )
                    data.id?.let  { viewModel.deleteData(it) }
                }
            }
            sendDataObserver()
        }

        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)

        if (connectedDevices.isNotEmpty()) {
            if (BLE.bluetoothDevice == null) {
                BLE.bluetoothDevice = connectedDevices.first()
            }
            bluetoothManager.adapter.getRemoteDevice(BLE.bluetoothDevice?.address)
            averageDataObserver()
            viewLifecycleOwner.lifecycleScope.launch {
                val bearer = viewModel.getBearer().first()
                viewModel.getAverage(bearer.toString())
            }
        } else {
            binding.layoutNotConnected.root.visibility = View.VISIBLE
            binding.layoutAverage.root.visibility = View.GONE
            binding.layoutLoading.root.visibility = View.GONE
        }

        if (!BackgroundMonitoringService.isRunning) {
            viewLifecycleOwner.lifecycleScope.launch {
                if (BLE.bluetoothDevice != null && viewModel.backgroundMonitoringState.first()) {
                    activity?.startService(Intent(activity, BackgroundMonitoringService::class.java))
                }
            }
        }

        binding.layoutNotConnected.btnConnect.setOnClickListener {
            startActivity(Intent(activity, ScannerActivity::class.java))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (connectedDevices.isNotEmpty()) {
                if (BLE.bluetoothDevice == null) {
                    BLE.bluetoothDevice = connectedDevices.first()
                }
                bluetoothManager.adapter.getRemoteDevice(BLE.bluetoothDevice?.address)
                averageDataObserver()
                viewLifecycleOwner.lifecycleScope.launch {
                    val bearer = viewModel.getBearer().first()
                    viewModel.getAverage(bearer.toString())
                }
            } else {
                binding.layoutNotConnected.root.visibility = View.VISIBLE
                binding.layoutAverage.root.visibility = View.GONE
                binding.layoutLoading.root.visibility = View.GONE
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val lineChart = binding.layoutAverage.lineChartHeartRate
        lineList = ArrayList()
        for (i in 0..24) {
            lineList.add(Entry(i.toFloat(), Random.nextFloat().times(100)))
        }
        lineDataSet = LineDataSet(lineList, "test")
        lineData = LineData(lineDataSet)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.valueFormatter = HourValueFormatter()
        lineChart.xAxis.setLabelCount(4, true)
        lineChart.xAxis.axisMinimum = 0F
        lineChart.xAxis.axisMaximum = 23.59F
        lineChart.data = lineData
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineDataSet.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.primary_dark_color), 250)
        lineDataSet.fillColor = ContextCompat.getColor(activity!!.applicationContext, R.color.primary_dark_color)
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineDataSet.setDrawFilled(true)
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawHighlightIndicators(true)
    }

    private fun sendDataObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sendData.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutAverage.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        binding.layoutLoading.root.visibility = View.GONE
                        binding.layoutAverage.root.visibility = View.VISIBLE
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
                        binding.layoutLoading.root.visibility = View.GONE
                        binding.layoutAverage.root.visibility = View.VISIBLE
                        binding.layoutAverage.tvAvgHeartText.text = getString(R.string.today_average_heart_rate, res.data?.avgHeartRate)
                        binding.layoutAverage.tvTodayStepsValue.text = res.data?.todaySteps.toString()
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