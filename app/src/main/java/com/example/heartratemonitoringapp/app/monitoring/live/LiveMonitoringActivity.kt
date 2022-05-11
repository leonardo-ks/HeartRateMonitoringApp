package com.example.heartratemonitoringapp.app.monitoring.live

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.app.monitoring.ble.BLE
import com.example.heartratemonitoringapp.app.monitoring.ble.UUIDs
import com.example.heartratemonitoringapp.databinding.ActivityLiveMonitoringBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.schedule


class LiveMonitoringActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveMonitoringBinding
    private val viewModel: LiveMonitoringViewModel by viewModel()
    private var device: BluetoothDevice? = null
    private val timer1 = Timer()
    private val timer2 = Timer()

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isLocationPermissionGranted()) {
            requestPermissions()
        }
        binding.layoutLoading.textLoading.text = "Menghubungkan"
        binding.layoutLoading.root.visibility = View.VISIBLE
        binding.layoutLiveMonitoring.root.visibility = View.GONE

        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        device = connectedDevices.first()
        binding.layoutLiveMonitoring.tvDevice.text = device?.name
        BLE.bluetoothGatt = device?.connectGatt(this, true, gattCallback)
        lifecycleScope.launch {
            timer1.schedule(0, 1000) {
                scanHeartRate()
            }
            timer2.schedule(0, 1000) {
                getStep()
            }
        }

        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            disconnect()
            finish()
        }

        viewModel.heartRateValue.observe(this) {
            binding.layoutLiveMonitoring.root.visibility = View.VISIBLE
            binding.layoutLoading.root.visibility = View.GONE
            binding.layoutLiveMonitoring.tvHeartRate.text = " $it BPM"
        }
        viewModel.stepValue.observe(this) {
            binding.layoutLiveMonitoring.tvStep.text = " $it Langkah"
        }
    }

    @SuppressLint("MissingPermission")
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    gatt?.discoverServices()
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (characteristic != null) {
                if (characteristic.uuid == UUIDs.BASIC_STEP_CHARACTERISTIC) {
                    val step = characteristic.value[1].toInt()
                    viewModel.updateStepValue(step)
                    Log.d("callback", "Step is: $step")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            val heartRateCharacteristic = gatt?.getService(UUIDs.HEART_RATE_SERVICE)?.getCharacteristic(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC)
            gatt?.setCharacteristicNotification(heartRateCharacteristic, true)

            val descriptor = heartRateCharacteristic?.getDescriptor(UUIDs.HEART_RATE_DESCRIPTOR)
            descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt?.writeDescriptor(descriptor)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            if (characteristic != null) {
                if (characteristic.uuid == UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC) {
                    val heartRate = characteristic.value[1].toInt()
                    viewModel.updateHeartRateValue(heartRate)
                    Log.d("callback", "Heart Rate is: $heartRate")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanHeartRate() {
        if (BLE.bluetoothGatt != null) {
            val bluetoothCharacteristic = BLE.bluetoothGatt?.getService(UUIDs.HEART_RATE_SERVICE)?.getCharacteristic(UUIDs.HEART_RATE_CONTROL_CHARACTERISTIC)
            if (bluetoothCharacteristic != null) {
                bluetoothCharacteristic.value = byteArrayOf(21, 1, 1)
                BLE.bluetoothGatt!!.writeCharacteristic(bluetoothCharacteristic)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getStep() {
        val basicService = BLE.bluetoothGatt?.getService(UUIDs.BASIC_SERVICE)
        if (basicService == null) {
            Log.d("basic service", "Step service not found!")
            return
        }
        val stepLevel = basicService.getCharacteristic(UUIDs.BASIC_STEP_CHARACTERISTIC)
        if (stepLevel == null) {
            Log.d("step level", "Step level not found!")
            return
        } else {
            BLE.bluetoothGatt?.readCharacteristic(stepLevel)
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        BLE.bluetoothGatt?.disconnect()
        timer1.cancel()
        timer1.purge()
        timer2.cancel()
        timer2.purge()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSION
        )
    }

    private fun isLocationPermissionGranted() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun onBackPressed() {
        disconnect()
        super.onBackPressed()
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }
}