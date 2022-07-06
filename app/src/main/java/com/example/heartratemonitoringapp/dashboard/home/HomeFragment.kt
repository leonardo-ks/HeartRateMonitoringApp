package com.example.heartratemonitoringapp.dashboard.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.monitoring.background.BackgroundMonitoringService
import com.example.heartratemonitoringapp.monitoring.ble.BLE
import com.example.heartratemonitoringapp.scanner.ScannerActivity
import com.example.heartratemonitoringapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
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

    @SuppressLint("MissingPermission")
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
                        binding.layoutAverage.tvAvgHeartValue.text = res.data?.avgHeartRate.toString()
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