package com.example.heartratemonitoringapp.app.dashboard.band

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.heartratemonitoringapp.app.monitoring.background.BackgroundMonitoringActivity
import com.example.heartratemonitoringapp.app.monitoring.live.LiveMonitoringActivity
import com.example.heartratemonitoringapp.app.scanner.ScannerActivity
import com.example.heartratemonitoringapp.databinding.FragmentBandBinding
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

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
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

        binding.layoutNotConnected.btnConnect.setOnClickListener {
            startActivity(Intent(activity, ScannerActivity::class.java))
        }

        binding.layoutBand.btnLiveMonitoring.setOnClickListener {
            startActivity(Intent(activity, LiveMonitoringActivity::class.java))
        }

        binding.layoutBand.btnBackgroundMonitoring.setOnClickListener {
            startActivity(Intent(activity, BackgroundMonitoringActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}