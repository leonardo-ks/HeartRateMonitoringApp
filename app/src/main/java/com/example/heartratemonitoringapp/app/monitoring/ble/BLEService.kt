package com.example.heartratemonitoringapp.app.monitoring.ble

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.heartratemonitoringapp.app.monitoring.ble.BLE.bluetoothAdapter
import com.example.heartratemonitoringapp.app.monitoring.ble.BLE.bluetoothGatt
import com.example.heartratemonitoringapp.util.toLittleEndian

class BLEService: Service() {

    private val binder = LocalBinder()

    fun initialize(): Boolean {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                broadcastUpdate(ACTION_GATT_CONNECTED, null)
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(ACTION_GATT_DISCONNECTED, null)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            val heartRateCharacteristic = gatt?.getService(UUIDs.HEART_RATE_SERVICE)?.getCharacteristic(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC)
            heartRateCharacteristic?.let { setCharacteristicNotification(it, true) }

            bluetoothGatt = gatt
            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, null)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (characteristic.uuid == UUIDs.BASIC_STEP_CHARACTERISTIC) {
                broadcastUpdate(ACTION_STEP_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC) {
                broadcastUpdate(ACTION_HR_AVAILABLE, characteristic)
            }
        }
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic?) {
        val intent = Intent(action)
        if (characteristic != null) {
            when (characteristic.uuid) {
                UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC -> {
                    val heartRate = characteristic.value[1].toInt()
                    Log.d("HR",heartRate.toString())
                    intent.putExtra("HR", heartRate)
                }
                UUIDs.BASIC_STEP_CHARACTERISTIC -> {
                    val data: ByteArray = characteristic.value
                    val hexString: String = data.joinToString("") {
                        String.format("%02X", it)
                    }
                    val step = toLittleEndian(hexString.slice(2..5))
                    Log.d("STEP", step.toString())
                    intent.putExtra("step", step)
                }
            }
        }
        sendBroadcast(intent)
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                return true
            } catch (exception: IllegalArgumentException) {
                Log.w(TAG, "Device not found with provided address.  Unable to connect.")
                return false
            }
        } ?: run {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return false
        }
    }

    @SuppressLint("MissingPermission")
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.readCharacteristic(characteristic) ?: run {
            Log.w(TAG, "BluetoothGatt not initialized")
        }
    }

    @SuppressLint("MissingPermission")
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.writeCharacteristic(characteristic) ?: run {
            Log.w(TAG, "BluetoothGatt not initialized")
        }
    }

    @SuppressLint("MissingPermission")
    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        bluetoothGatt?.let { gatt ->
            if (UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC == characteristic.uuid) {
                val descriptor = characteristic.getDescriptor(UUIDs.HEART_RATE_DESCRIPTOR)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
            gatt.setCharacteristicNotification(characteristic, enabled)
        } ?: run {
            Log.w(TAG, "BluetoothGatt not initialized")
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    @SuppressLint("MissingPermission")
    private fun close() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() : BLEService {
            return this@BLEService
        }
    }

    companion object {
        const val TAG = "BLEService"
        const val ACTION_GATT_CONNECTED =
            "com.example.heartratemonitoringapp.app.monitoring.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.heartratemonitoringapp.app.monitoring.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.heartratemonitoringapp.app.monitoring.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_STEP_AVAILABLE =
            "com.example.heartratemonitoringapp.app.monitoring.ACTION_STEP_AVAILABLE"
        const val ACTION_HR_AVAILABLE =
            "com.example.heartratemonitoringapp.app.monitoring.ACTION_HR_AVAILABLE"
    }
}