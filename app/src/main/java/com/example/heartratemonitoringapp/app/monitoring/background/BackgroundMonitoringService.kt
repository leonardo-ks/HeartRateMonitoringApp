package com.example.heartratemonitoringapp.app.monitoring.background

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.app.monitoring.ble.BLE
import com.example.heartratemonitoringapp.app.monitoring.ble.UUIDs
import java.util.*
import kotlin.concurrent.schedule

class BackgroundMonitoringService: Service() {

    private var device: BluetoothDevice? = null
    private val timer1 = Timer()
    private val timer2 = Timer()
    private val timer3 = Timer()
    private val heartList = arrayListOf<Int>()
    private val stepList = arrayListOf<Int>()
    private val channelId = "Background Monitoring"
    private lateinit var notificationManager: NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        device = connectedDevices.first()

        BLE.bluetoothGatt = device?.connectGatt(this, false, gattCallback)
        timer1.schedule(0, 1000) {
            scanHeartRate()
        }
        timer2.schedule(0, 1000) {
            getStep()
        }

        val notificationIntent = Intent(this, BackgroundMonitoringActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitoring latar belakang")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setContentText(getText())
        timer3.schedule(0, 60000) {
            if (heartList.size > 0 && stepList.size > 0) {
                Log.d("average", "${heartList.average().toInt()} ${stepList.last() - stepList.first()}")
                sendData(heartList.average().toInt(), stepList.lastIndex - stepList.first())
            }
            notification.setContentText(getText())
            notificationManager.notify(1, notification.build())
            heartList.clear()
            stepList.clear()
        }
        return START_STICKY
    }

    private fun getText(): String {
        return if (heartList.size > 0 && stepList.size > 0) {
            "Heartrate: ${heartList.last()}, Step: ${stepList.last()}"
        } else {
            "Monitoring sedang berjalan"
        }
    }

    override fun onDestroy() {
        timer1.cancel()
        timer1.purge()
        timer2.cancel()
        timer2.purge()
        timer3.cancel()
        timer3.purge()
        disconnect()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
        super.onDestroy()
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
                    stepList.add(step)
                    Log.d("callback", "Step: $step")
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
                    heartList.add(heartRate)
                    Log.d("callback", "Heart Rate: $heartRate")
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
    }

    fun sendData(avgHeart: Int, avgStep: Int) {

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}