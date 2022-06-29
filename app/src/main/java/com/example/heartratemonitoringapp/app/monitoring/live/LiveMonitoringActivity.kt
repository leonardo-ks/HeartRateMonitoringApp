package com.example.heartratemonitoringapp.app.monitoring.live

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.app.monitoring.ble.BLE
import com.example.heartratemonitoringapp.app.monitoring.ble.BLEService
import com.example.heartratemonitoringapp.app.monitoring.ble.UUIDs
import com.example.heartratemonitoringapp.databinding.ActivityLiveMonitoringBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.schedule


class LiveMonitoringActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLiveMonitoringBinding
    private var bluetoothService : BLEService? = null
    private val viewModel: LiveMonitoringViewModel by viewModel()
    private val timer1 = Timer()
    private val timer2 = Timer()
    private var timer1State = false
    private var timer2State = false

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            val binder = service as BLEService.LocalBinder
            bluetoothService = binder.getService()
            bluetoothService?.let { bluetooth ->
                if (!bluetooth.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth")
                    finish()
                }
                bluetooth.connect(BLE.bluetoothDevice?.address.toString())
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        title = TAG

        val bindIntent = Intent(this, BLEService::class.java)
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        if (!isLocationPermissionGranted()) {
            requestPermissions()
        }
        binding.layoutLoading.textLoading.text = getString(R.string.connecting)
        binding.layoutLoading.root.visibility = View.VISIBLE
        binding.layoutLiveMonitoring.root.visibility = View.GONE


        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        BLE.bluetoothAdapter = bluetoothManager.adapter
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)

        if (connectedDevices.isNotEmpty()) {
            if (BLE.bluetoothDevice == null) {
                BLE.bluetoothDevice = connectedDevices.first()
            }
            binding.layoutLiveMonitoring.tvDevice.text = BLE.bluetoothDevice?.name
        }

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            disconnect()
            finish()
        }

        viewModel.heartRateValue.observe(this) {
            binding.layoutLiveMonitoring.tvHeartRate.text = " $it BPM"
        }
        viewModel.stepValue.observe(this) {
            binding.layoutLiveMonitoring.tvStep.text = " $it ${getString(R.string.step)}"
        }
    }

    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BLEService.ACTION_GATT_CONNECTED -> {

                }
                BLEService.ACTION_GATT_DISCONNECTED -> {
                    disconnect()
                }
                BLEService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    if (!timer1State) {
                        timer1State = true
                        timer1.schedule(0, 10000) {
                            scanHeartRate()
                        }
                    }
                    if (!timer2State) {
                        timer2State = true
                        timer2.schedule(0, 2000) {
                            getStep()
                        }
                    }
                }
                BLEService.ACTION_HR_AVAILABLE -> {
                    binding.layoutLiveMonitoring.root.visibility = View.VISIBLE
                    binding.layoutLoading.root.visibility = View.GONE
                    if (intent.extras != null) {
                        val heartRate = intent.extras?.getInt("HR")
                        viewModel.updateHeartRateValue(heartRate ?: 0)
                    }
                }
                BLEService.ACTION_STEP_AVAILABLE -> {
                    if (intent.extras != null) {
                        val step = intent.extras?.getInt("step")
                        viewModel.updateStepValue(step ?: 0)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanHeartRate() {
        if (BLE.bluetoothGatt != null) {
            val bluetoothCharacteristic = BLE.bluetoothGatt?.getService(UUIDs.HEART_RATE_SERVICE)?.getCharacteristic(UUIDs.HEART_RATE_CONTROL_CHARACTERISTIC)
            bluetoothCharacteristic?.value = byteArrayOf(21, 1, 1)
            if (bluetoothCharacteristic != null) {
                bluetoothService?.writeCharacteristic(bluetoothCharacteristic)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getStep() {
        val basicService = BLE.bluetoothGatt?.getService(UUIDs.BASIC_SERVICE)
        val stepLevel = basicService?.getCharacteristic(UUIDs.BASIC_STEP_CHARACTERISTIC)
        if (stepLevel != null) {
            bluetoothService?.readCharacteristic(stepLevel)
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

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BLEService.ACTION_GATT_CONNECTED)
            addAction(BLEService.ACTION_GATT_DISCONNECTED)
            addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED)
            addAction(BLEService.ACTION_HR_AVAILABLE)
            addAction(BLEService.ACTION_STEP_AVAILABLE)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        if (bluetoothService != null) {
            val result = BLE.bluetoothDevice?.let { bluetoothService!!.connect(it.address) }
            Log.d(TAG, "Connect request result=$result")
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }

    override fun onBackPressed() {
        disconnect()
        super.onBackPressed()
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }

    companion object {
        const val TAG: String = "Live Monitoring Activity"
        const val REQUEST_CODE_PERMISSION = 1
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}