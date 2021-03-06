package com.example.heartratemonitoringapp.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.monitoring.ble.BLE
import com.example.heartratemonitoringapp.monitoring.ble.UUIDs
import com.example.heartratemonitoringapp.databinding.ActivityScannerBinding
import com.example.heartratemonitoringapp.scanner.adapter.ScannerAdapter
import kotlinx.coroutines.launch


class ScannerActivity : AppCompatActivity() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var binding: ActivityScannerBinding

    private var mAdapter = ScannerAdapter()
    private var isScanning = false
    val devices = arrayListOf<BluetoothDevice>()

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (!isLocationPermissionGranted()) {
            requestPermissions()
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBtIntent)
        }

        binding.layoutScanner.btnSearch.setOnClickListener {
            if (!isScanning) {
                isScanning = true
                Handler(Looper.getMainLooper()).postDelayed({
                    isScanning = false
                    bluetoothAdapter.bluetoothLeScanner.stopScan(mLeScanCallback)
                }, 60000)
                scanBLE()
                showRecycleView()
                binding.layoutScanner.btnSearch.text = getString(R.string.stop)
                binding.layoutScanner.btnSearch.icon = getDrawable(R.drawable.ic_stop)
            } else {
                isScanning = false
                val bluetoothLeScanner: BluetoothLeScanner  = bluetoothAdapter.bluetoothLeScanner
                bluetoothLeScanner.stopScan(mLeScanCallback)
                bluetoothLeScanner.flushPendingScanResults(mLeScanCallback)
                binding.layoutScanner.btnSearch.text = getString(R.string.search)
                binding.layoutScanner.btnSearch.icon = getDrawable(R.drawable.ic_search)
            }
        }

        mAdapter.onItemClick = {
            isScanning = false
            val bluetoothLeScanner: BluetoothLeScanner  = bluetoothAdapter.bluetoothLeScanner
            bluetoothLeScanner.stopScan(mLeScanCallback)
            bluetoothLeScanner.flushPendingScanResults(mLeScanCallback)
            binding.layoutScanner.btnSearch.text = "Cari"
            binding.layoutScanner.btnSearch.icon = getDrawable(R.drawable.ic_search)
            BLE.bluetoothDevice = it
            lifecycleScope.launch {
                BLE.bluetoothGatt = BLE.bluetoothDevice?.connectGatt(baseContext, true, gattCallback)
                binding.layoutLoading.textLoading.text = resources.getString(R.string.connecting)
                binding.layoutLoading.root.visibility = View.VISIBLE
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission", "SetTextI18n")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt?.discoverServices()
                pair()
            } else {
                gatt?.close()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun pair() {
        if (BLE.bluetoothGatt != null) {
            val service = BLE.bluetoothGatt?.getService(UUIDs.BASIC_SERVICE)
            val characteristic = service?.getCharacteristic(UUIDs.UUID_CHAR_PAIR)
            characteristic?.value = byteArrayOf(2)
            BLE.bluetoothGatt?.writeCharacteristic(characteristic)
        }
    }
    @SuppressLint("MissingPermission")
    private fun scanBLE() {
        val filters = ArrayList<ScanFilter>()
        val scanSettings = ScanSettings.Builder()
        scanSettings.setScanMode(ScanSettings.SCAN_MODE_BALANCED)

        bluetoothAdapter.bluetoothLeScanner.startScan(filters, scanSettings.build(), mLeScanCallback)
    }

    private val mLeScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result.device.name != null) {
                if (result.device.name.contains("Mi Band")) {
                    devices.add(result.device)
                }
            }
            mAdapter.setData(devices.distinct())
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
        }
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

    private fun showRecycleView() {
        binding.layoutScanner.rvBluetoothDevices.apply {
            layoutManager = LinearLayoutManager(this@ScannerActivity)
            !hasFixedSize()
            adapter = mAdapter
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}