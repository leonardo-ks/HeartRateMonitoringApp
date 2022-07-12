package com.example.heartratemonitoringapp.dashboard.band

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.FragmentBandBinding
import com.example.heartratemonitoringapp.monitoring.background.BackgroundMonitoringService
import com.example.heartratemonitoringapp.monitoring.ble.BLE
import com.example.heartratemonitoringapp.monitoring.live.LiveMonitoringActivity
import com.example.heartratemonitoringapp.scanner.ScannerActivity
import com.example.heartratemonitoringapp.util.HourValueFormatter
import com.example.heartratemonitoringapp.util.selected
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)

        if (connectedDevices.isNotEmpty()) {
            if (BLE.bluetoothDevice == null) {
                BLE.bluetoothDevice = connectedDevices.first()
            }
            bluetoothManager.adapter.getRemoteDevice(BLE.bluetoothDevice?.address)
            binding.layoutBand.root.visibility = View.VISIBLE
            binding.layoutBand.tvName.text = BLE.bluetoothDevice?.name
            binding.layoutBand.tvMac.text = BLE.bluetoothDevice?.address
        } else {

            binding.layoutNotConnected.root.visibility = View.VISIBLE
            binding.layoutBand.root.visibility = View.GONE
            binding.layoutLoading.root.visibility = View.GONE
        }

        val spinner = binding.layoutBand.layoutBandMenu.spinnerMonitoringPeriod
        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.periods, android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
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

        viewLifecycleOwner.lifecycleScope.launch {
            binding.layoutBand.layoutBandMenu.switchToogleMonitoring.isChecked = viewModel.backgroundMonitoringState.first()
            binding.layoutBand.layoutBandMenu.tidtUpperLimit.setText(viewModel.getUpper().first().toString())
            binding.layoutBand.layoutBandMenu.tidtLowerLimit.setText(viewModel.getLower().first().toString())
        }

//        binding.layoutNotConnected.btnConnect.setOnClickListener {
//            startActivity(Intent(activity, ScannerActivity::class.java))
//        }

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

    private fun openPowerSettings(context: Context) {
        val intent = Intent()
        val pm : PowerManager = context.getSystemService(POWER_SERVICE) as PowerManager

        val requestDialog = MaterialAlertDialogBuilder(context)
            .setMessage(getString(R.string.power_dialog))
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