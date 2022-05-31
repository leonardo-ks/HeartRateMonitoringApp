package com.example.heartratemonitoringapp.app.monitoring.background

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.text.method.TextKeyListener.clear
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.app.MainActivity
import com.example.heartratemonitoringapp.app.monitoring.ble.BLE
import com.example.heartratemonitoringapp.app.monitoring.ble.UUIDs
import com.example.heartratemonitoringapp.domain.usecase.IUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject
import org.koin.android.scope.serviceScope
import java.sql.Time
import java.time.Period
import java.util.*
import kotlin.concurrent.schedule

class BackgroundMonitoringService : Service() {

    private val timer = Timer()
    private val heartList = arrayListOf<Int>()
    private val stepList = arrayListOf<Int>()
    private val channelId = "Background Monitoring"
    private val useCase: IUseCase by inject()
    private lateinit var notificationManager: NotificationManager
    private lateinit var countDownTimer: CountDownTimer
    private var period: Long = 0
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(
                channelId,
                "Background Monitoring Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (BLE.bluetoothDevice != null) {
            BLE.bluetoothGatt = BLE.bluetoothDevice?.connectGatt(this, true, gattCallback)
        } else {
            val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
            BLE.bluetoothDevice = connectedDevices.first()
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitoring latar belakang")
            .setSmallIcon(R.drawable.ic_watch)
            .setContentIntent(pendingIntent)

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                notification.setContentText(getText())
                notificationManager.notify(1, notification.build())
                getStep()
            }

            override fun onFinish() {
                scope.launch {
                    if (heartList.size > 0 && stepList.size > 0) {
                        sendData(heartList.average().toInt(), stepList.last() - stepList.first())
                    }
                }
                notificationManager.cancel(1)
                cancel()
            }
        }

        scope.launch {
            period = useCase.getMonitoringPeriod().first().toLong()
            timer.schedule(0, period) {
                heartList.clear()
                stepList.clear()
                countDownTimer.start()
            }
        }

        return START_STICKY
    }

    private fun getText(): String {
        return if (heartList.size > 0 && stepList.size > 0) {
            "Heartrate: ${heartList.last()}, Step: ${stepList.last()}"
        } else {
            "Menghubungkan"
        }
    }

    override fun onDestroy() {
        clear()
        job.cancel()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    fun clear() {
        countDownTimer.cancel()
        timer.cancel()
        timer.purge()
        heartList.clear()
        stepList.clear()
        BLE.bluetoothGatt?.disconnect()
        notificationManager.deleteNotificationChannel(channelId)
    }

    @SuppressLint("MissingPermission")
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt?.discoverServices()
            } else {
                gatt?.close()
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
                    scanHeartRate()
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
            bluetoothCharacteristic?.value = byteArrayOf(21, 1, 1)
            BLE.bluetoothGatt?.writeCharacteristic(bluetoothCharacteristic)
        }
    }

    @SuppressLint("MissingPermission")
    fun getStep() {
        val basicService = BLE.bluetoothGatt?.getService(UUIDs.BASIC_SERVICE)
        val stepLevel = basicService?.getCharacteristic(UUIDs.BASIC_STEP_CHARACTERISTIC)
        BLE.bluetoothGatt?.readCharacteristic(stepLevel)
    }

    private fun sendData(avgHeart: Int, avgStep: Int) {
        scope.launch {
            useCase.addData(useCase.getBearer().first().toString(), avgHeart, avgStep, "")
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}