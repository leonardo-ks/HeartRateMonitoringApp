package com.example.heartratemonitoringapp.monitoring.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

object BLE {
    var bluetoothGatt: BluetoothGatt? = null
    var bluetoothDevice: BluetoothDevice? = null
    var bluetoothAdapter: BluetoothAdapter? = null
}