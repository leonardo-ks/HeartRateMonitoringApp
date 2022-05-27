package com.example.heartratemonitoringapp.app.dashboard.band

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.app.monitoring.background.BackgroundMonitoringService
import com.example.heartratemonitoringapp.app.monitoring.live.LiveMonitoringActivity
import com.example.heartratemonitoringapp.app.scanner.ScannerActivity
import com.example.heartratemonitoringapp.databinding.FragmentBandBinding
import com.example.heartratemonitoringapp.util.selected
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class BandFragment : Fragment() {
    private var _binding: FragmentBandBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBandBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission", "BatteryLife")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        val device: BluetoothDevice?
        if (connectedDevices.size < 1) {
            binding.layoutNotConnected.root.visibility = View.VISIBLE
            binding.layoutBand.root.visibility = View.GONE
        } else {
            device = connectedDevices.first()
            binding.layoutBand.root.visibility = View.VISIBLE
            binding.layoutBand.tvName.text = device?.name
            binding.layoutBand.tvMac.text = device?.address
        }

        val spinner = binding.layoutBand.layoutBandMenu.spinnerMonitoringPeriod
        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.periods, android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        lifecycleScope.launch {
            when (viewModel.monitoringPeriod.first()) {
                60000 -> spinner.setSelection(0)
                300000 -> spinner.setSelection(1)
                600000 -> spinner.setSelection(2)
                900000 -> spinner.setSelection(3)
            }
        }
        spinner.selected {
            when (it) {
                0 -> viewModel.setMonitoringPeriod(60000)
                1 -> viewModel.setMonitoringPeriod(300000)
                2 -> viewModel.setMonitoringPeriod(600000)
                3 -> viewModel.setMonitoringPeriod(900000)
            }
        }

        lifecycleScope.launch {
            binding.layoutBand.layoutBandMenu.switchToogleMonitoring.isChecked = viewModel.backgroundMonitoringState.first()
        }

        binding.layoutNotConnected.btnConnect.setOnClickListener {
            startActivity(Intent(activity, ScannerActivity::class.java))
        }

        binding.layoutBand.layoutBandMenu.cardLiveMonitoring.setOnClickListener {
            startActivity(Intent(activity, LiveMonitoringActivity::class.java))
        }

        binding.layoutBand.layoutBandMenu.switchToogleMonitoring.setOnClickListener {
            if (binding.layoutBand.layoutBandMenu.switchToogleMonitoring.isChecked) {
                context?.let { it1 -> openPowerSettings(it1) }
                viewModel.setBackgroundMonitoringState(true)
                activity?.startService(Intent(activity, BackgroundMonitoringService::class.java))
            } else {
                viewModel.setBackgroundMonitoringState(false)
                activity?.stopService(Intent(activity, BackgroundMonitoringService::class.java))
            }
        }
    }

    @SuppressLint("BatteryLife")
    private fun openPowerSettings(context: Context) {
        val intent = Intent()
        val pm : PowerManager = context.getSystemService(POWER_SERVICE) as PowerManager

        val requestDialog = MaterialAlertDialogBuilder(context)
            .setMessage("Agar dapat melakukan monitoring latar belakang aplikasi membutuhkan anda untuk menonaktifkan pengoptimalan daya. \nNonaktifkan pengoptimalan daya?")
            .setNegativeButton(getString(R.string.no)) { _, _ ->

            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                context.startActivity(intent)
            }
        if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
            requestDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}