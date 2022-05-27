package com.example.heartratemonitoringapp.app.dashboard.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.app.monitoring.background.BackgroundMonitoringService
import com.example.heartratemonitoringapp.app.scanner.ScannerActivity
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
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

        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        if (connectedDevices.size < 1) {
            binding.layoutNotConnected.root.visibility = View.VISIBLE
            binding.layoutAverage.root.visibility = View.GONE
            binding.layoutLoading.root.visibility = View.GONE
        } else {
            averageDataObserver()
            lifecycleScope.launch {
                val bearer = viewModel.getBearer().first()
                viewModel.getAverage(bearer.toString())
            }
        }

        lifecycleScope.launch {
            if (connectedDevices != null && viewModel.backgroundMonitoringState.first()) {
                activity?.startService(Intent(activity, BackgroundMonitoringService::class.java))
            }
        }

        binding.layoutNotConnected.btnConnect.setOnClickListener {
            startActivity(Intent(activity, ScannerActivity::class.java))
        }
    }

    @SuppressLint("MissingPermission")
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
                        binding.layoutLoading.root.visibility = View.GONE
                        binding.layoutAverage.root.visibility = View.VISIBLE
                        binding.layoutAverage.tvAvgHeartValue.text = res.data?.avgHeartRate.toString()
                        binding.layoutAverage.tvTodayStepsValue.text = res.data?.todaySteps.toString()
                    }
                    is Resource.Error -> {
                        binding.layoutAverage.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Snackbar.make(binding.root, res.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        if (connectedDevices.size < 1) {
            binding.layoutNotConnected.root.visibility = View.VISIBLE
            binding.layoutAverage.root.visibility = View.GONE
            binding.layoutLoading.root.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}