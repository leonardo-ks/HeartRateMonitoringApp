package com.example.heartratemonitoringapp.app.monitoring.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

object BLE {
    var bluetoothGatt: BluetoothGatt? = null
    var bluetoothDevice: BluetoothDevice? = null
}